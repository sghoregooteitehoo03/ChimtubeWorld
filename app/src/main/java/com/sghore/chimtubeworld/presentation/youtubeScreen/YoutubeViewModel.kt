package com.sghore.chimtubeworld.presentation.youtubeScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sghore.chimtubeworld.core.common.Resource
import com.sghore.chimtubeworld.core.common.suspendRunCatching
import com.sghore.chimtubeworld.core.domain.repository.YoutubeRepository
import com.sghore.chimtubeworld.core.model.Channel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class YoutubeViewModel @Inject constructor(
    private val repository: YoutubeRepository
) : ViewModel() {
    private val _state = MutableStateFlow(YoutubeScreenState(isLoading = true))
    val state: StateFlow<YoutubeScreenState> = _state.asStateFlow()

    init {
        getChannelInfo()
    }

    private fun loadChannels(): Flow<Resource<List<Channel?>>> = flow {
        suspendRunCatching {
            val channels = repository.getChannels()
            emit(Resource.Success<List<Channel?>>(channels))
        }.onFailure { e ->
            if (e !is Exception) throw e
            e.printStackTrace()
            emit(Resource.Error<List<Channel?>>("데이터를 불러오는데 오류가 발생하였습니다."))
        }
    }

    fun getChannelInfo() {
        loadChannels().onEach { resource ->
            when (resource) {
                is Resource.Success -> {
                    _state.update {
                        YoutubeScreenState(channels = resource.data)
                    }
                }
                is Resource.Loading -> {

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
