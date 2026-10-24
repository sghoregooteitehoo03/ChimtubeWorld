package com.sghore.chimtubeworld.feature.youtube.impl.playlist_screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.sghore.chimtubeworld.core.model.Playlist

@Composable
fun PlaylistRoute(
    viewModel: PlaylistViewModel,
    title: String,
    onBack: () -> Unit,
    onPlaylistClick: (Playlist) -> Unit
) {
    val uiState by viewModel.state.collectAsState()
    PlaylistScreen(
        uiState = uiState,
        title = title,
        onBack = onBack,
        onPlaylistClick = onPlaylistClick
    )
}
