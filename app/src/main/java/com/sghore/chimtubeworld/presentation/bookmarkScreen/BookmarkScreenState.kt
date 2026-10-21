package com.sghore.chimtubeworld.presentation.bookmarkScreen

import com.sghore.chimtubeworld.core.model.Video
import com.sghore.chimtubeworld.core.designsystem.R as DesignSystemR

data class BookmarkScreenState(
    val videoData: Video? = null,
    val videoTypeImage: Int = DesignSystemR.drawable.youtube,
    val bookmarkTitle: String = "",
    val videoPosition: String = "",
    val selectedColor: Int = android.graphics.Color.parseColor("#FF0000"),
    val isEnable: Boolean = false,
    val isLoading: Boolean = false,
    val isOpenDialog: Boolean = false
)
