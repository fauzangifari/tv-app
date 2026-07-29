# Code Review: MovieViewModel (AI-generated snippet)

Snippet yang direview (dari soal Part 3):

```kotlin
class MovieViewModel : ViewModel() {
    var movies: List<Movie> = emptyList()

    fun loadMovies() {
        val url = URL("https://api.example.com/movies")
        val data = url.readText()
        movies = parseMovies(data)
    }
}
```

## Masalah 1: Network call blocking di main thread

**Kenapa ini masalah:** `url.readText()` adalah operasi I/O synchronous/blocking.
`loadMovies()` tidak dipanggil di dalam coroutine atau dispatcher background,
jadi kalau dipanggil dari main thread, Android akan langsung throw
`NetworkOnMainThreadException` dan app crash bukan cuma freeze.

**Fix-nya:** Jalankan network call di dalam `viewModelScope.launch { ... }`,
dengan data source berupa `suspend fun` yang berjalan di `Dispatchers.IO`.

## Masalah 2: State tidak observable oleh Compose

**Kenapa ini masalah:** `movies` adalah `var` biasa, bukan `State`/`StateFlow`.
Compose cuma re-render UI kalau ada `State` yang dibaca selama composition
berubah nilainya. Assignment ke plain `var` tidak akan pernah trigger
recomposition, jadi UI tidak akan pernah menampilkan data baru meskipun
`movies` sudah terisi.

**Fix-nya:** Ganti jadi `StateFlow` yang di-collect di composable lewat
`collectAsStateWithLifecycle()`.

## Masalah 3: Tidak ada loading & error state

**Kenapa ini masalah:** Tidak ada indikator loading, dan tidak ada
try/catch di `loadMovies()`. Kalau network gagal atau `parseMovies(data)`
gagal parsing, exception-nya tidak ke-catch di mana pun dan akan crash
app. Requirement eksplisit minta 3 state (loading/error dengan
retry/success) — snippet ini tidak mengimplementasikan satupun.

**Fix-nya:** Bungkus dengan sealed state (`Loading`/`Error`/`Success`) dan
`runCatching` (atau try/catch) di sekitar network+parsing call, update
state sesuai hasilnya supaya UI bisa tampilkan retry button saat error.

## Masalah 4: ViewModel coupled langsung ke networking, tidak testable

**Kenapa ini masalah:** `loadMovies()` membuat `URL(...)` dan memanggil
`.readText()` langsung di dalam function-nya sendiri, tidak lewat
abstraksi/interface. Tidak ada constructor parameter untuk inject
dependency, jadi tidak ada seam untuk inject fake data saat unit test —
satu-satunya cara test `loadMovies()` adalah benar-benar hit network,
yang membuat test lambat dan flaky.

**Fix-nya:** Ekstrak logic fetch+parse ke interface repository, ViewModel
terima lewat constructor (`class MovieViewModel(private val repository:
MovieRepository)`). Saat unit test, inject `FakeMovieRepository` yang
mengembalikan data instan tanpa network.

## Masalah 5: Property publicly mutable, merusak single source of truth

**Kenapa ini masalah:** `var movies` bersifat public dan mutable dari
luar class. Class/composable manapun yang pegang reference ke ViewModel
ini bisa langsung menulis `viewModel.movies = someList` tanpa lewat
`loadMovies()`, membuat state ViewModel tidak predictable dan rawan race
condition kalau ada write bersamaan tanpa sinkronisasi.

**Fix-nya:** Expose read-only dari luar, mutasi hanya lewat function
ViewModel: `private val _movies = MutableStateFlow(...)` + `val movies:
StateFlow<List<Movie>> = _movies.asStateFlow()`.

## Masalah 6: Tidak pakai viewModelScope, tidak ada cancellation

**Kenapa ini masalah:** Tidak ada `Job` yang bisa dibatalkan. Kalau user
navigasi keluar sebelum fetch selesai, request tetap jalan sampai
selesai — buang resource, bahkan bisa mencoba update state di ViewModel
yang sudah tidak dipakai lagi.

**Fix-nya:** Pindahkan logic ke `viewModelScope.launch { ... }` supaya
lifecycle-nya otomatis ter-cancel begitu `onCleared()` dipanggil.

## Revisi setelah semua fix diterapkan

Pola ini sama persis dengan yang sudah dipakai di app ini sendiri
(`ShowListViewModel` + `ShowsRepository` + `UiState`):

```kotlin
sealed interface MovieUiState {
    data object Loading : MovieUiState
    data class Success(val movies: List<Movie>) : MovieUiState
    data class Error(val message: String) : MovieUiState
}

interface MovieRepository {
    suspend fun getMovies(): Result<List<Movie>>
}

class MovieViewModel(
    private val repository: MovieRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<MovieUiState>(MovieUiState.Loading)
    val uiState: StateFlow<MovieUiState> = _uiState.asStateFlow()

    init {
        loadMovies()
    }

    fun loadMovies() {
        viewModelScope.launch {
            _uiState.value = MovieUiState.Loading
            repository.getMovies()
                .onSuccess { movies -> _uiState.value = MovieUiState.Success(movies) }
                .onFailure { error ->
                    _uiState.value = MovieUiState.Error(error.message ?: "Something went wrong")
                }
        }
    }
}
```

`MovieRepository` diimplementasikan pakai Retrofit + kotlinx.serialization
(atau library sejenis), bukan `URL.readText()` manual — sama seperti
`ShowsRepositoryImpl` di app ini membungkus `ApiService` dengan
`runCatching`.

---