package com.android.tvapp.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ShowDto(
    val id: Int,
    val name: String,
    val url: String? = null,
    val image: ImageDto? = null,
    val rating: RatingDto? = null,
    val summary: String? = null,
    val premiered: String? = null,
    @SerialName("_embedded")
    val embedded: EmbeddedDto? = null
)
