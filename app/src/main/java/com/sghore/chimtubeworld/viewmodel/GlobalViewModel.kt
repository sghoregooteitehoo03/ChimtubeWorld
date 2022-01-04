package com.sghore.chimtubeworld.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sghore.chimtubeworld.data.Goods
import com.sghore.chimtubeworld.data.Video

class GlobalViewModel : ViewModel() {
    val showGoodsList = MutableLiveData<List<Goods>?>(null) // 굿즈 리스트
    val selectedGoodsPos = MutableLiveData(-1) // 선택한 굿즈 위치
    val videoData = MutableLiveData<Video?>(null) // 영상 데이터
    val refreshList = MutableLiveData(false) // 리스트 새로고침
}