package com.sghore.chimtubeworld.presentation.bookmarkScreen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.sghore.chimtubeworld.R
import com.sghore.chimtubeworld.data.model.Bookmark
import com.sghore.chimtubeworld.data.model.Video

data class BookmarkScreenState(
    val videoData: Video? = null,
    val videoTypeImage: Int = R.drawable.youtube,
    val isLoading: Boolean = false,
    val completeBookmark: Bookmark? = null,
    val errorMsg: String = ""
) {
    var bookmarkInfoState by mutableStateOf(BookmarkInfoState())
}

data class BookmarkInfoState(
    val bookmarkTitle: String = "",
    val videoPosition: String = "",
    val selectedColor: Int = android.graphics.Color.parseColor("#FF0000"),
    val isEnable: Boolean = false
)