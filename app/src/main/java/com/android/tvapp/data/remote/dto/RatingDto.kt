package com.android.tvapp.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class RatingDto(
    val average: Double? = null
)
