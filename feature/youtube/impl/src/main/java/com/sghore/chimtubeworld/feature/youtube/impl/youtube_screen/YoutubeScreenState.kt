package com.sghore.chimtubeworld.feature.youtube.impl.youtube_screen

import com.sghore.chimtubeworld.core.model.Channel

data class YoutubeScreenState(
    val isLoading: Boolean = false,
    val channels: List<Channel?>? = null,
    val errorMsg: String = ""
)
