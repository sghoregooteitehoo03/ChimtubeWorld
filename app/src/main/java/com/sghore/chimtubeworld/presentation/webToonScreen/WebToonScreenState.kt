package com.sghore.chimtubeworld.presentation.webToonScreen

import com.sghore.chimtubeworld.data.model.Channel

data class WebToonScreenState(
    val webtoos: List<Channel>? = null,
    val isLoading: Boolean = false,
    val errorMsg: String = ""
)
