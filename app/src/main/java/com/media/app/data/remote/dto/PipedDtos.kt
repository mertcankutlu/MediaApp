package com.media.app.data.remote.dto

import com.google.gson.annotations.SerializedName

data class PipedSearchResultDto(
    @SerializedName("url") val url: String?,
    @SerializedName("title") val title: String?,
    @SerializedName("uploaderName") val uploaderName: String?,
    @SerializedName("thumbnail") val thumbnail: String?,
    @SerializedName("duration") val duration: Long?
)

data class PipedStreamResponseDto(
    @SerializedName("audioStreams") val audioStreams: List<PipedAudioStreamDto>?
)

data class PipedAudioStreamDto(
    @SerializedName("url") val url: String?,
    @SerializedName("format") val format: String?,
    @SerializedName("bitrate") val bitrate: Int?,
    @SerializedName("quality") val quality: String?
)
