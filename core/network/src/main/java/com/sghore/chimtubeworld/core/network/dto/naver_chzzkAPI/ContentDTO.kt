package com.sghore.chimtubeworld.core.network.dto.naver_chzzkAPI

data class ContentDTO(
    val topExposedVideos: TopExposedVideosDTO? = null,
    val channelId: String = "",
    val channelName: String = "",
    val channelImageUrl: String = "",
    val followerCount: Int = 0,
    val openLive: Boolean = false
)