package com.sghore.chimtubeworld.presentation.playlistScreen

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.sghore.chimtubeworld.domain.GetPagingMainPlaylistUseCase
import com.sghore.chimtubeworld.domain.GetPagingSubPlaylistUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlaylistViewModel @Inject constructor(
    private val getPagingMainPlaylistUseCase: GetPagingMainPlaylistUseCase,
    private val getPagingSubPlaylistUseCase: GetPagingSubPlaylistUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _state = MutableStateFlow(PlaylistScreenState())
    val state = _state.asStateFlow()

    init {
        val channelId = savedStateHandle.get<String>("channelId")
        val playlistId = savedStateHandle.get<String>("playlistId")
            ?.split("|")
            ?: emptyList()
        val type = savedStateHandle.get<Int>("type")

        if (type == 0) {
            getPagingMainPlaylist(channelId, playlistId)
        } else {
            getPagingSubPlaylist(playlistId)
        }
    }

    fun getPagingMainPlaylist(channelId: String?, playlistId: List<String>) {
        _state.update {
            it.copy(
                playlists = getPagingMainPlaylistUseCase(
                    channelId = channelId,
                    playlistId = playlistId
                ).cachedIn(viewModelScope)
            )
        }
    }

    fun getPagingSubPlaylist(playlistId: List<String>) {
        _state.update {
            it.copy(
                playlists = getPagingSubPlaylistUseCase(
                    playlistId = playlistId
                ).cachedIn(viewModelScope)
            )
        }
    }
}