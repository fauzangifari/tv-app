# TvApp

Aplikasi Android untuk menjelajahi daftar TV show beserta detailnya, dibangun dengan Jetpack
Compose dan mengonsumsi [TVmaze API](https://www.tvmaze.com/api).

## Video Walkthrough

[![Video Walkthrough](https://img.shields.io/badge/Google%20Drive-Tonton%20Video-blue?logo=googledrive&logoColor=white)](https://drive.google.com/file/d/1_qh0eK76kTvaDBeeJEVjQQ5CWrw9R01_/view?usp=sharing)

## Fitur

- Daftar TV show dengan infinite scroll pagination
- Pull-to-refresh di layar daftar dan detail
- Halaman detail show (rating, genre, cast, episode)
- Share show lewat share sheet bawaan Android
- Rendering deskripsi HTML dari API

## Arsitektur

Project ini mengikuti layered architecture (data / domain / ui):

```
data/
  remote/       -> ApiService (Retrofit) & DTO
  mapper/       -> konversi DTO ke domain model
  repository/   -> implementasi repository
domain/
  model/        -> domain model
  repository/   -> kontrak repository
ui/
  list/         -> layar daftar show (Screen + ViewModel)
  detail/       -> layar detail show (Screen + ViewModel)
  navigation/   -> NavGraph
  common/       -> komponen UI yang dipakai bersama
di/             -> dependency provisioning manual (tanpa Hilt)
```

## Tech Stack

- Kotlin & Jetpack Compose (Material 3)
- Retrofit + OkHttp + kotlinx.serialization
- Navigation Compose
- Coil untuk image loading
- Coroutines & ViewModel (androidx.lifecycle)
- JUnit + kotlinx-coroutines-test untuk unit test

## Menjalankan Project

1. Clone repository ini.
2. Buka project di Android Studio.
3. Sync Gradle, lalu jalankan konfigurasi `app` pada emulator/device dengan minimum SDK 29.

## Dokumen Lain

- [AI_LOG.md](AI_LOG.md) — catatan penggunaan AI selama pengerjaan
- [CODE_REVIEW.md](CODE_REVIEW.md) — hasil code review
- [REFLECTION.md](REFLECTION.md) — refleksi pengerjaan project
