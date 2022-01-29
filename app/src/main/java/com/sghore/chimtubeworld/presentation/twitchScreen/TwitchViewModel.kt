package com.sghore.chimtubeworld.presentation.twitchScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sghore.chimtubeworld.data.model.Resource
import com.sghore.chimtubeworld.domain.GetTwitchChannelUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class TwitchViewModel @Inject constructor(
    private val getTwitchChannelUseCase: GetTwitchChannelUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(TwitchScreenState(isLoading = true))
    val state: StateFlow<TwitchScreenState> = _state.asStateFlow()

    init {
        getTwitchUserInfo()
    }

    fun getTwitchUserInfo() {
        getTwitchChannelUseCase().onEach { resource ->
            when (resource) {
                is Resource.Success -> {
                    _state.update {
                        TwitchScreenState(
                            mainChannelInfo = resource.data?.filter { it?.type == 0 }?.get(0),
                            channels = resource.data
                        )
                    }
                }
                is Resource.Loading -> {
                    _state.update {
                        TwitchScreenState(isLoading = true)
                    }
                }
                is Resource.Error -> {
                    _state.update {
                        TwitchScreenState(errorMsg = resource.errorMsg ?: "오류")
                    }
                }
            }
        }.launchIn(viewModelScope)
    }
}