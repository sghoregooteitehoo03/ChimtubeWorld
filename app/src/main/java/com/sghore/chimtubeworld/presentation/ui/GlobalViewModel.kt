package com.sghore.chimtubeworld.presentation.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sghore.chimtubeworld.data.model.Bookmark
import com.sghore.chimtubeworld.data.model.Goods
import com.sghore.chimtubeworld.data.model.Video
import com.sghore.chimtubeworld.other.Contents

class GlobalViewModel : ViewModel() {
    val videoData = MutableLiveData<Video?>(null) // 영상 데이터
    val bookmarkData = mutableStateOf<Bookmark?>(null) // 북마크 데이터
    var topAppBarAction by mutableStateOf("") // 툴바 아이콘 액션
}