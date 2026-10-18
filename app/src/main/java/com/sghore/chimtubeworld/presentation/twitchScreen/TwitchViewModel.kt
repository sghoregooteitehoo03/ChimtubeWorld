package com.sghore.chimtubeworld.presentation.twitchScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sghore.chimtubeworld.core.common.Resource
import com.sghore.chimtubeworld.core.common.suspendRunCatching
import com.sghore.chimtubeworld.core.domain.repository.TwitchRepository
import com.sghore.chimtubeworld.core.model.Channel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class TwitchViewModel @Inject constructor(
    private val repository: TwitchRepository
) : ViewModel() {
    private val _state = MutableStateFlow(TwitchScreenState(isLoading = true))
    val state: StateFlow<TwitchScreenState> = _state.asStateFlow()

    init {
        getTwitchUserInfo()
    }

    private fun loadChannels(): Flow<Resource<List<Channel>>> = flow {
        suspendRunCatching {
            val channels = repository.getChannels()
            emit(Resource.Success<List<Channel>>(channels))
        }.onFailure { e ->
            if (e !is Exception) throw e
            e.printStackTrace()
            emit(Resource.Error<List<Channel>>("데이터를 불러오는데 오류가 발생하였습니다."))
        }
    }

    fun getTwitchUserInfo() {
        loadChannels().onEach { resource ->
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
