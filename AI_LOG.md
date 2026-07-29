## Entry 1 kotlinx.serialization vs Gson

**Ditanya:** Kenapa network layer pakai kotlinx.serialization bukan Gson (yang lebih
umum dipakai), dan di mana JSON-to-object conversion-nya kejadian.

**Dikasih:** Gson pakai reflection runtime, bisa diam-diam masukin null ke field
non-null (baru ketauan sebagai NPE nanti). kotlinx.serialization pakai compiler
plugin, enforce nullability langsung pas parsing.

**Dilakukan:** Diterima. Tetap pakai kotlinx.serialization di seluruh data layer.
Relevan karena rating.average emang wajib null-safe sesuai requirement soal.

**Verifikasi sendiri:** Cek dokumentasi resmi kotlinx.serialization (field
non-nullable wajib ada/non-null, kalau nggak throw exception) dan bandingin sama
known issue Gson+Kotlin null-safety. Rencana buktiin lewat unit test: kasih JSON
rating.average: null, cek RatingDto ke-decode clean tanpa crash.

## Entry 2 Perlu UseCase layer atau tidak

**Ditanya:** Sebelum bikin ViewModel, saya tanya apakah perlu nambah lapisan
UseCase di domain (di antara Repository dan ViewModel), soalnya itu pola umum
di Clean Architecture yang sering diajarin.

**Dikasih:** Jawaban pertama masih dua sisi (bisa iya bisa nggak, tergantung
mau nunjukkin familiarity sama pattern-nya) belum kasih rekomendasi tegas.
Baru pas saya minta jawaban jelas, AI merekomendasikan skip UseCase, karena
untuk app ini use case-nya bakal cuma pass-through 1:1 ke repository tanpa
logic tambahan.

**Dilakukan:** Diterima. ViewModel manggil `ShowsRepository` langsung, gak
ada lapisan UseCase.

**Verifikasi sendiri:** Yang saya catat sebagai "AI kurang tepat" di sini
bukan soal teknis, tapi cara jawabnya: respons pertama kurang tegas/masih
menggantung dua opsi, jadi saya yang harus dorong minta rekomendasi
langsung. Keputusan akhirnya saya setujui karena alasannya masuk akal
(proporsional sama scope app + sesuai requirement soal), bukan cuma ikut
saran begitu saja.

## Entry 3 Fallback URL Share ditaruh di tempat yang salah

**Ditanya:** Saya minta AI jelasin asumsi di balik field `url` buat
fitur Share. AI ngaku field itu dibikin nullable dengan fallback
`https://www.tvmaze.com/shows/{id}` (tanpa slug) kalau `url` dari API
null, dan fallback itu ditulis langsung di composable `shareShow()`
di UI layer.

**Dikasih:** Saya tanya balik "kamu gada buat mapper kah?" — soalnya
project ini udah punya `ShowDto.toDomain()` di `ShowsRepositoryImpl`
yang jadi satu-satunya tempat konversi DTO ke domain model. Fallback
URL itu logic data layer (nentuin apa "url" show itu), bukan logic
UI, jadi harusnya nyatu di mapper, bukan ditulis lagi terpisah di
composable Share.

**Dilakukan:** Diterima dan dibenerin, tapi dua tahap. Tahap pertama,
AI mindahin fallback ke `toDomain()` — tapi fungsi itu masih nempel
sebagai `private fun` di dalam `ShowsRewpositoryImpl`, bukan komponen
mapper yang berdiri sendiri. Saya cek lagi dan bilang ke AI "kamu
gada buat mapper kah?" — karena kalau cuma private function di dalam
repository, itu bukan mapper yang sebenarnya, cuma helper lokal.
Baru di situ AI ekstrak `toDomain()` jadi top-level function di file
baru `data/mapper/Mapper.kt`, dan `ShowsRepositoryImpl` tinggal
manggil `apiService....toDomain()` tanpa tau detail konversinya.

**Verifikasi sendiri:** Ini nunjukkin AI bisa "benerin" sesuatu
setengah-setengah — fallback-nya udah pindah ke logic yang benar,
tapi struktur filenya belum benar-benar mapper terpisah sampai saya
tegasin lagi. Kalau nanti ada DTO lain yang butuh konversi serupa,
sekarang tempatnya jelas satu file, gak perlu digali dari dalam
repository. Setelah dipisah, saya compile ulang buat mastiin gak ada
yang break dan importnya bersih (`ShowsRepositoryImpl` gak lagi
import DTO/domain model yang gak dipakai langsung).

## Entry 4 Bug `remember` tanpa key di infinite scroll pagination

**Ditanya:** Saya baca-baca ulang kode pagination yang udah saya buat di
`ShowListScreen.kt`, dan ngerasa ada yang aneh di bagian
`remember { derivedStateOf { ... } }` — kayaknya `shows.size` di
dalamnya gak bakal ke-update pas datanya nambah. Saya minta AI
konfirmasi apa itu beneran bug atau cuma perasaan saya aja.

**Dikasih:** AI nemuin bug di `ShowGrid` (`ShowListScreen.kt`), di
logic infinite scroll yang AI sendiri yang nulis pas nambah fitur
pagination:
```kotlin
val shouldLoadMore by remember {
    derivedStateOf {
        val lastVisibleIndex = gridState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
        lastVisibleIndex >= shows.size - 4
    }
}
```
`remember { ... }` tanpa key cuma jalanin lambda-nya sekali di
komposisi pertama, padahal `derivedStateOf` di dalamnya baca
`shows.size` yang berubah tiap halaman pagination baru kemuat.
Akibatnya closure-nya kebeku pakai `shows` dari 250 item awal
selamanya — begitu user scroll lewat index ~246 sekali aja,
`shouldLoadMore` jadi `true` terus tanpa pernah balik `false`,
bikin app auto-fetch semua halaman berturut-turut walau user diam di
satu posisi scroll.

**Dilakukan:** Diterima dan langsung dibenerin: tambahin `shows.size`
sebagai key ke `remember(shows.size) { derivedStateOf { ... } }` biar
closure-nya dibuat ulang tiap daftarnya berubah.

**Verifikasi sendiri:** Ini bug yang paling "berbahaya" yang saya
tangkep sejauh ini karena lolos dari compile check DAN dari testing
manual biasa (kelihatan jalan normal kalau scroll-nya gak pernah
lewat index ~246, atau kalau cuma dicek sekali dua kali). Baru
ketauan karena saya minta AI review ulang kodenya sendiri secara
spesifik, bukan nunggu ketauan pas testing. Saya pastiin
pemahamannya bener dengan telusuri manual: kenapa `remember` tanpa
key gak re-run lambda-nya, dan kenapa itu bikin `shows` yang
ke-capture di closure jadi permanen nunjuk ke list pertama. Setelah
fix, saya compile ulang buat mastiin gak ada yang break.