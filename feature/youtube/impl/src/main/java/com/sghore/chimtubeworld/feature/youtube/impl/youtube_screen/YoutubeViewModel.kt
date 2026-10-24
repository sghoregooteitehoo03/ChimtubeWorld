package com.sghore.chimtubeworld.feature.youtube.impl.youtube_screen

import androidx.lifecycle.viewModelScope
import com.sghore.chimtubeworld.core.common.suspendRunCatching
import com.sghore.chimtubeworld.core.domain.repository.YoutubeRepository
import com.sghore.chimtubeworld.core.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
internal class YoutubeViewModel @Inject constructor(
    private val repository: YoutubeRepository
) : BaseViewModel<YoutubeUiState, YoutubeUiEvent, YoutubeUiSideEffect>(YoutubeUiState()) {

    init {
        loadChannels()
    }

    override fun onEvent(event: YoutubeUiEvent) {
        when (event) {
            is YoutubeUiEvent.ChannelClicked -> postSideEffect(YoutubeUiSideEffect.NavigateToPlaylists(event.channel))
        }
    }

    private fun loadChannels() {
        viewModelScope.launch {
            reduce { copy(isLoading = true, hasLoadError = false) }

            suspendRunCatching {
                repository.getChannels()
            }.onSuccess { channels ->
                reduce { YoutubeUiState(channels = channels) }
            }.onFailure { error ->
                if (error !is Exception) throw error
                error.printStackTrace()

                reduce { copy(isLoading = false, hasLoadError = true) }
            }
        }
    }
}
