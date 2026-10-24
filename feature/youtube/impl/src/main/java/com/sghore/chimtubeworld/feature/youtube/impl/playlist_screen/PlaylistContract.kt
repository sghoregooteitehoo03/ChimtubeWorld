package com.sghore.chimtubeworld.feature.youtube.impl.playlist_screen

import androidx.paging.PagingData
import com.sghore.chimtubeworld.core.model.Playlist
import com.sghore.chimtubeworld.core.ui.base.UiEvent
import com.sghore.chimtubeworld.core.ui.base.UiSideEffect
import com.sghore.chimtubeworld.core.ui.base.UiState
import kotlinx.coroutines.flow.Flow

internal data class PlaylistUiState(
    val title: String = "",
    val playlists: Flow<PagingData<Playlist>>? = null
) : UiState

internal sealed interface PlaylistUiEvent : UiEvent {
    data object BackClicked : PlaylistUiEvent
    data class PlaylistClicked(val playlist: Playlist) : PlaylistUiEvent
}

internal sealed interface PlaylistUiSideEffect : UiSideEffect {
    data object NavigateBack : PlaylistUiSideEffect
    data class NavigateToVideos(val playlist: Playlist) : PlaylistUiSideEffect
}
