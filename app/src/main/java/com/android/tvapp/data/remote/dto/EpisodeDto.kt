package com.android.tvapp.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class EpisodeDto(
    val id: Int,
    val name: String,
    val season: Int,
    val number: Int? = null,
    val airdate: String? = null,
    val image: ImageDto? = null
)
