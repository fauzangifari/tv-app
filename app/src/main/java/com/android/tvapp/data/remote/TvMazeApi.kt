package com.android.tvapp.data.remote

import com.android.tvapp.data.remote.dto.ShowDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TvMazeApi {

    @GET("shows")
    suspend fun getShows(@Query("page") page: Int = 0): List<ShowDto>

    @GET("shows/{id}")
    suspend fun getShowDetail(@Path("id") id: Int): ShowDto
}
