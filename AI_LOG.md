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