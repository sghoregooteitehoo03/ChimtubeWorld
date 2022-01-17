package com.sghore.chimtubeworld.presentation.youtubeScreen

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sghore.chimtubeworld.data.model.Channel
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
    private val _state = mutableStateOf(YoutubeScreenState())
    val state: State<YoutubeScreenState> = _state

    init {
        getChannelInfo()
    }

    fun getChannelInfo() = viewModelScope.launch {
        getYoutubeChannelUseCase().onEach { resource ->
            when (resource) {
                is Resource.Success -> {
                    _state.value = YoutubeScreenState(channels = resource.data)
                }
                is Resource.Loading -> {
                    _state.value = YoutubeScreenState(isLoading = true)
                }
                is Resource.Error -> {
                    _state.value = YoutubeScreenState(errorMsg = resource.errorMsg ?: "오류")
                }
            }
        }.launchIn(viewModelScope)
    }
}