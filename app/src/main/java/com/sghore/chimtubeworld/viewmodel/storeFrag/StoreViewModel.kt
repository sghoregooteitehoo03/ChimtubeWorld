package com.sghore.chimtubeworld.viewmodel.storeFrag

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sghore.chimtubeworld.data.Channel
import com.sghore.chimtubeworld.data.Goods
import com.sghore.chimtubeworld.data.Post
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StoreViewModel @Inject constructor(
    private val repository: StoreRepository
) : ViewModel() {
    private val _isLoading = MutableLiveData(true) // 로딩 여부
    private val _storeImages = MutableLiveData<List<Channel>>(listOf()) // 스토어 정보
    private val _goodsList = MutableLiveData<List<Goods>?>(null) // 선택된 스토어에 제공하고 있는 상품리스트

    val isLoading: LiveData<Boolean> = _isLoading
    val storeImages: LiveData<List<Channel>> = _storeImages
    val goodsList: LiveData<List<Goods>?> = _goodsList

    val selectedPos = MutableLiveData(0) // 선택된 스토어에 위치
    val currentUrl = MutableLiveData("") // 선택된 스토어에 url

    // 스토어의 정보를 가져옴
    fun getStoreImages() = viewModelScope.launch {
        _storeImages.value = CoroutineScope(Dispatchers.IO).async {
            repository.getStoreInfo()
        }.await()
        _isLoading.value = false // 로딩 끝
    }

    fun getGoodsList(url: String) = viewModelScope.launch {
        _goodsList.value = listOf() // 리스트 초기화
        _goodsList.value = CoroutineScope(Dispatchers.IO).async {
            repository.getGoodsList(url)
        }.await()
    }
}