package com.sghore.chimtubeworld.presentation.bookmarkScreen

import com.sghore.chimtubeworld.R
import com.sghore.chimtubeworld.data.model.Video

data class BookmarkScreenState(
    val videoData: Video? = null,
    val videoTypeImage: Int = R.drawable.youtube,
    val bookmarkTitle: String = "",
    val videoPosition: String = "",
    val selectedColor: Int = android.graphics.Color.parseColor("#FF0000"),
    val isEnable: Boolean = false,
    val isLoading: Boolean = false,
    val isOpenDialog: Boolean = false
)