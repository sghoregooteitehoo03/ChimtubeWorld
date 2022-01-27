package com.sghore.chimtubeworld.presentation.youtubeScreen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sghore.chimtubeworld.data.model.Resource
import com.sghore.chimtubeworld.domain.GetYoutubeChannelUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class YoutubeViewModel @Inject constructor(
    private val getYoutubeChannelUseCase: GetYoutubeChannelUseCase
) : ViewModel() {
    var state by mutableStateOf(YoutubeScreenState())
        private set

    init {
        getChannelInfo()
    }

    fun getChannelInfo() = viewModelScope.launch {
        getYoutubeChannelUseCase().onEach { resource ->
            state = when (resource) {
                is Resource.Success -> {
                    YoutubeScreenState(channels = resource.data)
                }
                is Resource.Loading -> {
                    YoutubeScreenState(isLoading = true)
                }
                is Resource.Error -> {
                    YoutubeScreenState(errorMsg = resource.errorMsg ?: "오류")
                }
            }
        }.launchIn(viewModelScope)
    }
}