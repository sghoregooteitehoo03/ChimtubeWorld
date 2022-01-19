package com.sghore.chimtubeworld.presentation.twitchScreen

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sghore.chimtubeworld.data.model.Channel
import com.sghore.chimtubeworld.data.model.Resource
import com.sghore.chimtubeworld.data.repository.TwitchRepository
import com.sghore.chimtubeworld.domain.GetTwitchChannelUseCase
import com.sghore.chimtubeworld.presentation.youtubeScreen.YoutubeScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class TwitchViewModel @Inject constructor(
    private val getTwitchChannelUseCase: GetTwitchChannelUseCase
) : ViewModel() {
    private val _state = mutableStateOf(TwitchScreenState())
    val state: State<TwitchScreenState> = _state

    init {
        getTwitchUserInfo()
    }

    fun getTwitchUserInfo() {
        getTwitchChannelUseCase().onEach { resource ->
            when (resource) {
                is Resource.Success -> {
                    _state.value = TwitchScreenState(
                        mainChannelInfo = resource.data?.filter { it?.type == 0 }?.get(0),
                        channels = resource.data
                    )
                }
                is Resource.Loading -> {
                    _state.value = TwitchScreenState(isLoading = true)
                }
                is Resource.Error -> {
                    _state.value = TwitchScreenState(errorMsg = resource.errorMsg ?: "오류")
                }
            }
        }.launchIn(viewModelScope)
    }
}