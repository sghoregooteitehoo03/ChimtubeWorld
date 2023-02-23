package com.sghore.chimtubeworld.data.retrofit.dto.twitchAPI

import com.google.gson.annotations.SerializedName

data class StreamDataDTO(
    @SerializedName("game_name") val gameName: String,
    val type: String,
    val title: String,
    @SerializedName("thumbnail_url") val thumbnailUrl: String,
    val user_login: String
) {
}