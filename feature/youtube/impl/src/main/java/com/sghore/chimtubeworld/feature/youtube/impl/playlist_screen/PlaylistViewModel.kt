package com.sghore.chimtubeworld.feature.youtube.impl.playlist_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.sghore.chimtubeworld.core.domain.repository.YoutubeRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

@HiltViewModel(assistedFactory = PlaylistViewModel.Factory::class)
class PlaylistViewModel @AssistedInject constructor(
    private val repository: YoutubeRepository,
    @Assisted("channelId") private val channelId: String?,
    @Assisted("playlistId") private val playlistId: String,
    @Assisted private val type: Int
) : ViewModel() {
    private val _state = MutableStateFlow(PlaylistScreenState())
    val state = _state.asStateFlow()

    init {
        val playlistIds = playlistId.split("|")

        if (type == 0) {
            getPagingMainPlaylist(channelId, playlistIds)
        } else {
            getPagingSubPlaylist(playlistIds)
        }
    }

    fun getPagingMainPlaylist(channelId: String?, playlistId: List<String>) {
        _state.update {
            it.copy(
                playlists = repository.getPagingMainPlaylist(
                    channelId = channelId,
                    playlistId = playlistId
                ).cachedIn(viewModelScope)
            )
        }
    }

    fun getPagingSubPlaylist(playlistId: List<String>) {
        _state.update {
            it.copy(
                playlists = repository.getPagingSubPlaylist(
                    playlistId = playlistId
                ).cachedIn(viewModelScope)
            )
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted("channelId") channelId: String?,
            @Assisted("playlistId") playlistId: String,
            type: Int
        ): PlaylistViewModel
    }
}
