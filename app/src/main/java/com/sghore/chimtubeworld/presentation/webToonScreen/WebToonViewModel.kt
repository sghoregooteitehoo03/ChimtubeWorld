package com.sghore.chimtubeworld.presentation.webToonScreen

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sghore.chimtubeworld.data.model.Channel
import com.sghore.chimtubeworld.data.repository.WebToonRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import javax.inject.Inject

@HiltViewModel
class WebToonViewModel @Inject constructor(
    private val repository: WebToonRepository
) : ViewModel() {
    private val _webToonList = MutableLiveData<List<Channel>?>(null) // 웹툰 리스트
    private val _isLoading = MutableLiveData(true) // 로딩 여부
    val webToonList: LiveData<List<Channel>?> = _webToonList
    val isLoading: LiveData<Boolean> = _isLoading

    fun getWebToonList() = viewModelScope.launch {
        _webToonList.value = CoroutineScope(Dispatchers.IO).async {
            repository.getWebToonList()
        }.await()
        _isLoading.value = false // 로딩 끝
    }
}