package com.sghore.chimtubeworld.presentation.ui

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sghore.chimtubeworld.data.model.Bookmark
import com.sghore.chimtubeworld.data.model.Goods
import com.sghore.chimtubeworld.data.model.Video

class GlobalViewModel : ViewModel() {
    val videoData = MutableLiveData<Video?>(null) // 영상 데이터
    val bookmarkData = mutableStateOf<Bookmark?>(null) // 북마크 데이터
}