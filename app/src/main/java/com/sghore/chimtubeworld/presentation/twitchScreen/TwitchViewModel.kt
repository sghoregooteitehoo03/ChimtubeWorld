package com.sghore.chimtubeworld.presentation.twitchScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sghore.chimtubeworld.core.common.Resource
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
                        val channels = resource.data
                        TwitchScreenState(
                            mainChannelInfo = channels!!.filter { it.type == 0 }[0],
                            channels = channels
                        )
                    }
                }
                is Resource.Loading -> {

                }
                is Resource.Error -> {
                    _state.update {
                        TwitchScreenState(errorMsg = resource.errorMsg ?: "오류")
                    }
                }
            }
        }.launchIn(viewModelScope)
    }

    fun setDialog(isDialogOpen: Boolean) {
        _state.update {
            it.copy(isDialogOpen = isDialogOpen)
        }
    }
}
