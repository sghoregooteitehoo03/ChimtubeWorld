package com.sghore.chimtubeworld.presentation.playlistScreen

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.sghore.chimtubeworld.domain.GetPlaylistsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class PlaylistViewModel @Inject constructor(
    private val getPlaylistsUseCase: GetPlaylistsUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _state = MutableStateFlow(PlaylistScreenState())
    val state = _state.asStateFlow()

    init {
        val channelId = savedStateHandle.get<String>("channelId")
        val playlistId = savedStateHandle.get<String>("playlistId")
            ?.split("|")
            ?: emptyList()

        getPlaylists(
            channelId = channelId,
            playlistId = playlistId
        )
    }

    fun getPlaylists(channelId: String?, playlistId: List<String>) {
        _state.update {
            it.copy(
                playlists = getPlaylistsUseCase.invoke(
                    channelId = channelId,
                    playlistId = playlistId
                ).cachedIn(viewModelScope)
            )
        }
    }
}