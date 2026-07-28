package com.android.tvapp.domain.model

data class Show(
    val id: Int,
    val title: String,
    val posterUrl: String?,
    val fullPosterUrl: String?,
    val rating: Double?,
    val summary: String?,
    val premiered: String?,
    val cast: List<CastMember> = emptyList(),
    val episodes: List<Episode> = emptyList()
)
