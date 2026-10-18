package com.sghore.chimtubeworld.presentation.youtubeScreen

import com.sghore.chimtubeworld.core.model.Channel

data class YoutubeScreenState(
    val isLoading: Boolean = false,
    val channels: List<Channel?>? = null,
    val errorMsg: String = ""
)
