package com.sghore.chimtubeworld.feature.chzzk.impl.chzzk_screen

import com.sghore.chimtubeworld.core.model.Channel

internal data class TwitchScreenState(
    val mainChannelInfo: Channel? = null,
    val channels: List<Channel?> = emptyList(),
    val isLoading: Boolean = false,
    val isDialogOpen: Boolean = false,
    val errorMsg: String = ""
)
