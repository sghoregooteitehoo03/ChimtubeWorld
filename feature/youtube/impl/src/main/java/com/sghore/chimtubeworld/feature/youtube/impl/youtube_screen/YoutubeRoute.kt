package com.sghore.chimtubeworld.feature.youtube.impl.youtube_screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.collectLatest

@Composable
internal fun YoutubeRoute(
    viewModel: YoutubeViewModel = hiltViewModel(),
    onSideEffect: (YoutubeUiSideEffect) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val currentOnSideEffect by rememberUpdatedState(onSideEffect)

    LaunchedEffect(viewModel.sideEffect) {
        viewModel.sideEffect.collectLatest { currentOnSideEffect(it) }
    }

    YoutubeScreen(uiState = uiState, onEvent = viewModel::onEvent)
}
