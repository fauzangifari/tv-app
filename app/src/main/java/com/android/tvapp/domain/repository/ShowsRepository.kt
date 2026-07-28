package com.android.tvapp.domain.repository

import com.android.tvapp.domain.model.Show

interface ShowsRepository {
    suspend fun getShows(page: Int = 0): Result<List<Show>>
    suspend fun getShowDetail(id: Int): Result<Show>
}
