package com.android.tvapp.domain.model

data class Episode(
    val name: String,
    val season: Int,
    val number: Int?,
    val airdate: String?
)
