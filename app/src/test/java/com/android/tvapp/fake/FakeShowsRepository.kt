package com.android.tvapp.fake

import com.android.tvapp.domain.model.Show
import com.android.tvapp.domain.repository.ShowsRepository

class FakeShowsRepository : ShowsRepository {

    var showsResult: Result<List<Show>> = Result.success(emptyList())
    val nextPageResults: MutableMap<Int, Result<List<Show>>> = mutableMapOf()
    var showDetailResult: Result<Show> = Result.failure(Exception("Not implemented"))

    override suspend fun getShows(page: Int): Result<List<Show>> {
        if (page == 0) return showsResult
        return nextPageResults[page] ?: Result.success(emptyList())
    }

    override suspend fun getShowDetail(id: Int): Result<Show> {
        return showDetailResult
    }
}
