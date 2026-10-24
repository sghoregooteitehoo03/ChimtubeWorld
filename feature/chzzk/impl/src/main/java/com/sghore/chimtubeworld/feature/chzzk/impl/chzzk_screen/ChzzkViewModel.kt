package com.sghore.chimtubeworld.feature.chzzk.impl.chzzk_screen

import androidx.lifecycle.viewModelScope
import com.sghore.chimtubeworld.core.common.suspendRunCatching
import com.sghore.chimtubeworld.core.domain.repository.TwitchRepository
import com.sghore.chimtubeworld.core.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class ChzzkViewModel @Inject constructor(
    private val repository: TwitchRepository
) : BaseViewModel<ChzzkUiState, ChzzkUiEvent, ChzzkUiSideEffect>(ChzzkUiState()) {

    init {
        loadChannels()
    }

    override fun onEvent(event: ChzzkUiEvent) {
        when (event) {
            ChzzkUiEvent.MainChannelClicked -> reduce { copy(isDialogOpen = true) }

            ChzzkUiEvent.DialogDismissed -> reduce { copy(isDialogOpen = false) }

            ChzzkUiEvent.YoutubeClicked -> {
                reduce { copy(isDialogOpen = false) }

                postSideEffect(ChzzkUiSideEffect.OpenYoutube("https://www.youtube.com/@ChimChakMan_Data"))
            }

            ChzzkUiEvent.ChzzkClicked -> {
                reduce { copy(isDialogOpen = false) }

                postSideEffect(ChzzkUiSideEffect.OpenUrl(currentState.mainChannelInfo?.url ?: ""))
            }

            is ChzzkUiEvent.CrewChannelClicked -> {
                postSideEffect(ChzzkUiSideEffect.OpenUrl(event.channel.url))
            }
        }
    }

    private fun loadChannels() {
        viewModelScope.launch {
            reduce { copy(isLoading = true, hasLoadError = false) }

            suspendRunCatching {
                repository.getChannels()
            }.onSuccess { channels ->
                // 기존과 동일하게 첫 type == 0 채널을 사용하며, 없으면 예외를 전파한다.
                val mainChannel = channels.filter { it.type == 0 }[0]

                reduce {
                    ChzzkUiState(mainChannelInfo = mainChannel, channels = channels)
                }
            }.onFailure { error ->
                if (error !is Exception) throw error
                error.printStackTrace()

                reduce { copy(isLoading = false, hasLoadError = true) }
            }
        }
    }
}
