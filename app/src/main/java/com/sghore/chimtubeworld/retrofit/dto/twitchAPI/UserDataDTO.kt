package com.sghore.chimtubeworld.retrofit.dto.twitchAPI

import com.google.gson.annotations.SerializedName

data class UserDataDTO(
    val id: String,
    val login: String,
    @SerializedName("display_name") val displayName: String,
    val profile_image_url: String,
    val offline_image_url: String
)
