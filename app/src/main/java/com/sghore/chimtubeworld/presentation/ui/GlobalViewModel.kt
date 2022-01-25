package com.sghore.chimtubeworld.presentation.ui

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sghore.chimtubeworld.data.model.Bookmark
import com.sghore.chimtubeworld.data.model.Goods
import com.sghore.chimtubeworld.data.model.Video

class GlobalViewModel : ViewModel() {
    val showGoodsList = MutableLiveData<List<Goods>?>(null) // 굿즈 리스트
    val selectedGoodsPos = MutableLiveData(-1) // 선택한 굿즈 위치
    val videoData = MutableLiveData<Video?>(null) // 영상 데이터
    val bookmarkData = mutableStateOf<Bookmark?>(null) // 북마크 데이터
}