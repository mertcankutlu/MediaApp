package com.media.app.data.remote.api

import com.media.app.data.remote.dto.PipedSearchResultDto
import com.media.app.data.remote.dto.PipedStreamResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface PipedApiService {

    @GET("search")
    suspend fun search(
        @Query("q") query: String,
        @Query("filter") filter: String = "music_songs"
    ): List<PipedSearchResultDto>

    @GET("nextpage/search")
    suspend fun searchNextPage(
        @Query("q") query: String,
        @Query("nextpage") nextPage: String,
        @Query("filter") filter: String = "music_songs"
    ): List<PipedSearchResultDto>

    @GET("streams")
    suspend fun getStreams(
        @Query("v") videoId: String
    ): PipedStreamResponseDto
}
