package com.sghore.chimtubeworld.feature.youtube.impl.playlist_screen

import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.sghore.chimtubeworld.core.domain.repository.YoutubeRepository
import com.sghore.chimtubeworld.core.ui.base.BaseViewModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel

@HiltViewModel(assistedFactory = PlaylistViewModel.Factory::class)
internal class PlaylistViewModel @AssistedInject constructor(
    private val repository: YoutubeRepository,
    @Assisted("channelId") private val channelId: String?,
    @Assisted("playlistId") private val playlistId: String,
    @Assisted private val type: Int,
    @Assisted("title") title: String
) : BaseViewModel<PlaylistUiState, PlaylistUiEvent, PlaylistUiSideEffect>(PlaylistUiState(title = title)) {

    init {
        val ids = playlistId.split("|")
        val playlists = if (type == 0) {
            repository.getPagingMainPlaylist(channelId, ids)
        } else {
            repository.getPagingSubPlaylist(ids)
        }.cachedIn(viewModelScope)

        reduce { copy(playlists = playlists) }
    }

    override fun onEvent(event: PlaylistUiEvent) {
        when (event) {
            PlaylistUiEvent.BackClicked -> postSideEffect(PlaylistUiSideEffect.NavigateBack)
            is PlaylistUiEvent.PlaylistClicked -> postSideEffect(PlaylistUiSideEffect.NavigateToVideos(event.playlist))
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted("channelId") channelId: String?,
            @Assisted("playlistId") playlistId: String,
            type: Int,
            @Assisted("title") title: String
        ): PlaylistViewModel
    }
}
