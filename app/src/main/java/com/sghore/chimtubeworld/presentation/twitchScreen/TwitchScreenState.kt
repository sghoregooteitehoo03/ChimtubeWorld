package com.sghore.chimtubeworld.presentation.twitchScreen

import com.sghore.chimtubeworld.data.model.Channel

data class TwitchScreenState(
    val mainChannelInfo: Channel? = null,
    val channels: List<Channel?> = emptyList(),
    val isLoading: Boolean = false,
    val errorMsg: String = ""
)
