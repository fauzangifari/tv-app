package com.android.tvapp.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class CastDto(
    val person: PersonDto,
    val character: CharacterDto
)
