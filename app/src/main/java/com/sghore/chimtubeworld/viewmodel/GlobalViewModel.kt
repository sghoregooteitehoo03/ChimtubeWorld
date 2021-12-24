package com.sghore.chimtubeworld.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sghore.chimtubeworld.data.Goods

class GlobalViewModel : ViewModel() {
    val showGoodsList = MutableLiveData<List<Goods>?>(null)
    val selectedGoodsPos = MutableLiveData(-1)
}