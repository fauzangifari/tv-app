package com.android.tvapp.data.repository

import com.android.tvapp.data.remote.ApiService
import com.android.tvapp.data.remote.dto.ShowDto
import com.android.tvapp.domain.model.CastMember
import com.android.tvapp.domain.model.Episode
import com.android.tvapp.domain.model.Show
import com.android.tvapp.domain.repository.ShowsRepository

class ShowsRepositoryImpl(
    private val apiService: ApiService
) : ShowsRepository {

    override suspend fun getShows(page: Int): Result<List<Show>> = runCatching {
        apiService.getShows(page).map { it.toDomain() }
    }

    override suspend fun getShowDetail(id: Int): Result<Show> = runCatching {
        apiService.getShowDetail(id).toDomain()
    }

    private fun ShowDto.toDomain(): Show = Show(
        id = id,
        title = name,
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
                number = episode.number
            )
        }
    )
}
