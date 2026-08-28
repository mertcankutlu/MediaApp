package com.media.app.data.remote.api

import com.media.app.data.remote.dto.PipedSearchResultDto
import com.media.app.data.remote.dto.PipedStreamResponseDto
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

interface PipedApiService {

    @GET
    suspend fun search(
        @Url fullUrl: String,
        @Query("q") query: String,
        @Query("filter") filter: String = "music_songs"
    ): List<PipedSearchResultDto>

    @GET
    suspend fun getStreams(
        @Url fullUrl: String,
        @Query("v") videoId: String
    ): PipedStreamResponseDto
}
