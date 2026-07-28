package com.android.tvapp.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class EmbeddedDto(
    val cast: List<CastDto>? = null,
    val episodes: List<EpisodeDto>? = null
)
