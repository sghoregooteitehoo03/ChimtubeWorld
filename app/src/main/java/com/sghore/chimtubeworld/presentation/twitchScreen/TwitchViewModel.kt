package com.sghore.chimtubeworld.presentation.twitchScreen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sghore.chimtubeworld.data.model.Resource
import com.sghore.chimtubeworld.domain.GetTwitchChannelUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class TwitchViewModel @Inject constructor(
    private val getTwitchChannelUseCase: GetTwitchChannelUseCase
) : ViewModel() {
    var state by mutableStateOf(TwitchScreenState())
        private set

    init {
        getTwitchUserInfo()
    }

    fun getTwitchUserInfo() {
        getTwitchChannelUseCase().onEach { resource ->
            state = when (resource) {
                is Resource.Success -> {
                    TwitchScreenState(
                        mainChannelInfo = resource.data?.filter { it?.type == 0 }?.get(0),
                        channels = resource.data
                    )
                }
                is Resource.Loading -> {
                    TwitchScreenState(isLoading = true)
                }
                is Resource.Error -> {
                    TwitchScreenState(errorMsg = resource.errorMsg ?: "오류")
                }
            }
        }.launchIn(viewModelScope)
    }
}