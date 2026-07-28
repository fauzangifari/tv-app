package com.android.tvapp.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class ShowDto(
    val id: Int,
    val name: String,
    val image: ImageDto? = null,
    val rating: RatingDto? = null,
    val summary: String? = null,
    val premiered: String? = null
)
