package com.sghore.chimtubeworld.feature.youtube.impl.youtube_screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.sghore.chimtubeworld.core.model.Channel

@Composable
fun YoutubeRoute(
    onChannelClick: (Channel) -> Unit,
    viewModel: YoutubeViewModel = hiltViewModel()
) {
    val uiState by viewModel.state.collectAsState()
    YoutubeScreen(uiState = uiState, onChannelClick = onChannelClick)
}
