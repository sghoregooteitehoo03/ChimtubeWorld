package com.sghore.chimtubeworld.viewmodel.youtubeFrag

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sghore.chimtubeworld.data.Channel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class YoutubeViewModel @Inject constructor(
    private val repository: YoutubeRepository
) : ViewModel() {
    private val _isLoading = MutableLiveData(true) // 로딩 여부
    private val _allChannelList = MutableLiveData<List<Channel?>>(null) // 채널 리스트

    val allChannelList: LiveData<List<Channel?>> = _allChannelList
    val isLoading: LiveData<Boolean> = _isLoading

    fun getChannelInfo() = viewModelScope.launch {
        try {
            val channelList = repository.getChannelInfo()

            _allChannelList.value = channelList.toList()
            _isLoading.value = false // 로딩 끝
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}