package com.android.tvapp.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class PersonDto(
    val id: Int,
    val name: String,
    val image: ImageDto? = null
)
