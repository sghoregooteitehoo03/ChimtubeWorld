package com.sghore.chimtubeworld.viewmodel.twitchFrag

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sghore.chimtubeworld.data.Channel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class TwitchViewModel @Inject constructor(
    private val repository: TwitchRepository
) : ViewModel() {

    private val _twitchUserList = MutableLiveData<List<Channel>?>(null)
    private val _mainChannelData = MutableLiveData<Channel?>(null)
    val twitchUserList: LiveData<List<Channel>?> = _twitchUserList
    val mainChannelData: LiveData<Channel?> = _mainChannelData

    fun getTwitchUserInfo() = viewModelScope.launch {
        try { // 문제가 발생하지 않았을 때
            val userList = repository.getTwitchUserInfo()

            _twitchUserList.value = userList
            _mainChannelData.value = userList.filter { it.type == 0 }[0]
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}