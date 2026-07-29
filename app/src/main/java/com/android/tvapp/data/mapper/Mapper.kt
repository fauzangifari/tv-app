package com.android.tvapp.data.mapper

import com.android.tvapp.data.remote.dto.ShowDto
import com.android.tvapp.domain.model.CastMember
import com.android.tvapp.domain.model.Episode
import com.android.tvapp.domain.model.Show

fun ShowDto.toDomain(): Show = Show(
    id = id,
    title = name,
    url = url ?: "https://www.tvmaze.com/shows/$id",
    posterUrl = image?.medium,
    fullPosterUrl = image?.original,
    rating = rating?.average,
    summary = summary,
    premiered = premiered,
    cast = embedded?.cast.orEmpty().map { cast ->
        CastMember(
            personName = cast.person.name,
            characterName = cast.character.name,
            imageUrl = cast.person.image?.medium
        )
    },
    episodes = embedded?.episodes.orEmpty().map { episode ->
        Episode(
            name = episode.name,
            season = episode.season,
            number = episode.number,
            airdate = episode.airdate
        )
    }
)
