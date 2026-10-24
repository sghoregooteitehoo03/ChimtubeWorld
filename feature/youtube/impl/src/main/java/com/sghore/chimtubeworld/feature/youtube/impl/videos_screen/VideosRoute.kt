package com.sghore.chimtubeworld.feature.youtube.impl.videos_screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.collectLatest

@Composable
internal fun VideosRoute(
    viewModel: VideosViewModel,
    onSideEffect: (VideosUiSideEffect) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val currentOnSideEffect by rememberUpdatedState(onSideEffect)

    LaunchedEffect(viewModel.sideEffect) {
        viewModel.sideEffect.collectLatest { currentOnSideEffect(it) }
    }

    VideosScreen(uiState = uiState, onEvent = viewModel::onEvent)
}
