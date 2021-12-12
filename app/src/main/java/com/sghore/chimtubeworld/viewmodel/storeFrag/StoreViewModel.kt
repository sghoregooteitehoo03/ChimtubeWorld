package com.sghore.chimtubeworld.viewmodel.storeFrag

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sghore.chimtubeworld.data.Channel
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

    private val _storeImages = MutableLiveData<List<Channel>>(listOf())
    private val _goodsList = MutableLiveData<List<Post>?>(null)

    val storeImages: LiveData<List<Channel>> = _storeImages
    val goodsList: LiveData<List<Post>?> = _goodsList

    fun getStoreImages() = viewModelScope.launch {
        _storeImages.value = CoroutineScope(Dispatchers.IO).async {
            repository.getStoreInfo()
        }.await()
    }

    fun getGoodsList() = viewModelScope.launch {
        _goodsList.value = CoroutineScope(Dispatchers.IO).async {
            repository.getGoodsList("https://much-merch.com/product/list.html?cate_no=299")
        }.await()
    }
}