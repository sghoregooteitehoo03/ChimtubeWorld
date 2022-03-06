package com.sghore.chimtubeworld.presentation.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.sghore.chimtubeworld.data.model.Bookmark

class GlobalViewModel : ViewModel() {
    var bookmarkData: Bookmark? = null // 북마크 데이터
    var topAppBarAction by mutableStateOf("") // 툴바 아이콘 액션
}