package com.sghore.chimtubeworld.feature.bookmark.impl.bookmark_screen

import com.sghore.chimtubeworld.core.model.Video
import com.sghore.chimtubeworld.core.model.VideoSource

data class BookmarkScreenState(
    val videoData: Video? = null,
    val videoSource: VideoSource? = VideoSource.YOUTUBE,
    val bookmarkTitle: String = "",
    val videoPosition: String = "",
    val selectedColor: Int = android.graphics.Color.parseColor("#FF0000"),
    val isEnable: Boolean = false,
    val isLoading: Boolean = false,
    val isOpenDialog: Boolean = false
)
