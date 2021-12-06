package com.sghore.chimtubeworld.retrofit.dto.twitchAPI

import com.google.gson.annotations.SerializedName

data class StreamDataDTO(
    @SerializedName("game_name") val gameName: String,
    val type: String,
    val title: String,
    @SerializedName("thumbnail_url") val thumbnailUrl: String
) {
}