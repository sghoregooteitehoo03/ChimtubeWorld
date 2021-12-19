package com.sghore.chimtubeworld.retrofit.dto.twitchAPI

import com.google.gson.annotations.SerializedName

data class VideosDataDTO(
    val id: String,
    val title: String,
    @SerializedName("published_at") val publishedAt: String,
    @SerializedName("thumbnail_url") val thumbnailUrl: String,
    @SerializedName("view_count") val viewCount: Long,
    @SerializedName("duration") val duration: String,
    val url: String
)
