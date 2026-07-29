package com.android.tvapp.data.repository

import com.android.tvapp.data.mapper.toDomain
import com.android.tvapp.data.remote.ApiService
import com.android.tvapp.domain.model.Show
import com.android.tvapp.domain.repository.ShowsRepository
import retrofit2.HttpException

class ShowsRepositoryImpl(
    private val apiService: ApiService
) : ShowsRepository {

    override suspend fun getShows(page: Int): Result<List<Show>> = runCatching {
        apiService.getShows(page).map { it.toDomain() }
    }.recoverCatching { error ->
        if (error is HttpException && error.code() == 404) emptyList() else throw error
    }

    override suspend fun getShowDetail(id: Int): Result<Show> = runCatching {
        apiService.getShowDetail(id).toDomain()
    }
}
