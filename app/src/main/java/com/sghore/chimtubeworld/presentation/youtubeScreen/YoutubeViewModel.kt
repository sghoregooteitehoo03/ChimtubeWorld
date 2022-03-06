package com.sghore.chimtubeworld.presentation.youtubeScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sghore.chimtubeworld.data.model.Resource
import com.sghore.chimtubeworld.domain.GetYoutubeChannelUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class YoutubeViewModel @Inject constructor(
    private val getYoutubeChannelUseCase: GetYoutubeChannelUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(YoutubeScreenState(isLoading = true))
    val state: StateFlow<YoutubeScreenState> = _state.asStateFlow()

    init {
        getChannelInfo()
    }

    fun getChannelInfo() {
        getYoutubeChannelUseCase().onEach { resource ->
            when (resource) {
                is Resource.Success -> {
                    _state.update {
                        YoutubeScreenState(channels = resource.data)
                    }
                }
                is Resource.Loading -> {
                    _state.update {
                        YoutubeScreenState(isLoading = true)
                    }
                }
                is Resource.Error -> {
                    _state.update {
                        YoutubeScreenState(errorMsg = resource.errorMsg ?: "오류")
                    }
                }
            }
        }.launchIn(viewModelScope)
    }
}