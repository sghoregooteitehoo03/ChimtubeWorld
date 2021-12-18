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

    private val _allChannelList = MutableLiveData<List<Channel?>>(null)
    val allChannelList: LiveData<List<Channel?>> = _allChannelList

    fun getChannelInfo() = viewModelScope.launch {
        try {
            val channelList = repository.getChannelInfo()
            _allChannelList.value = channelList.toList()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}