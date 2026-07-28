## Entry 1 kotlinx.serialization vs Gson

**Ditanya:** Kenapa network layer pakai kotlinx.serialization bukan Gson (yang lebih
umum dipakai), dan di mana JSON-to-object conversion-nya kejadian.

**Dikasih:** Gson pakai reflection runtime, bisa diam-diam masukin null ke field
non-null (baru ketauan sebagai NPE nanti). kotlinx.serialization pakai compiler
plugin, enforce nullability langsung pas parsing.

**Dilakukan:** Diterima tetap pakai kotlinx.serialization di seluruh data layer.
Relevan karena rating.average emang wajib null-safe sesuai requirement soal.

**Verifikasi sendiri:** Cek dokumentasi resmi kotlinx.serialization (field
non-nullable wajib ada/non-null, kalau nggak throw exception) dan bandingin sama
known issue Gson+Kotlin null-safety. Rencana buktiin lewat unit test: kasih JSON
rating.average: null, cek RatingDto ke-decode clean tanpa crash.