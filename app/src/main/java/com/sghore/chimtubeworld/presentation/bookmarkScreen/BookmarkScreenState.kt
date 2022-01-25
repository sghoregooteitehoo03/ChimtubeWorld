package com.sghore.chimtubeworld.presentation.bookmarkScreen

import com.sghore.chimtubeworld.R
import com.sghore.chimtubeworld.data.model.Bookmark
import com.sghore.chimtubeworld.data.model.Video

data class BookmarkScreenState(
    val bookmarkTitle: String = "",
    val videoPosition: String = "",
    val videoData: Video? = null,
    val videoTypeImage: Int = R.drawable.ic_youtube,
    val selectedColor: Int = android.graphics.Color.parseColor("#FF0000"),
    val isEnable: Boolean = false,
    val isLoading: Boolean = false,
    val completeBookmark: Bookmark? = null,
    val errorMsg: String = ""
)
