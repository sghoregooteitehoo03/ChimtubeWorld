package com.sghore.chimtubeworld.presentation.storeDetailScreen

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sghore.chimtubeworld.data.model.Goods
import com.sghore.chimtubeworld.data.repository.StoreDetailRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StoreDetailViewModel @Inject constructor(
    private val repository: StoreDetailRepository
) : ViewModel() {
    // 프리뷰 이미지 리스트
    private val _previewImages = MutableLiveData<List<String>>(null)
    val previewImages: LiveData<List<String>> = _previewImages

    val isLoading = MutableLiveData(true) // 로딩 여부
    val selectedPos = MutableLiveData(-1)
    val selectedImage = MutableLiveData<String>(null) // 선택된 이미지

    // 프리뷰 이미지들을 가져옴
    fun getStoreDetail(goods: Goods) = viewModelScope.launch {
        _previewImages.value = CoroutineScope(Dispatchers.IO).async {
            repository.getStoreDetail(goods)
        }.await()!!
        isLoading.value = false // 로딩 끝
    }
}