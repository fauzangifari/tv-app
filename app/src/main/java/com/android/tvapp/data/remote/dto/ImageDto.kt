package com.android.tvapp.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class ImageDto(
    val medium: String? = null,
    val original: String? = null
)
