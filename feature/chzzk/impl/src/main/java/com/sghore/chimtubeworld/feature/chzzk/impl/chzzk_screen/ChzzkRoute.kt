package com.sghore.chimtubeworld.feature.chzzk.impl.chzzk_screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberUpdatedState
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.collectLatest

@Composable
internal fun ChzzkRoute(
    viewModel: ChzzkViewModel = hiltViewModel(),
    onSideEffect: (ChzzkUiSideEffect) -> Unit
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle()
    val currentOnSideEffect by rememberUpdatedState(onSideEffect)

    LaunchedEffect(viewModel.sideEffect) {
        viewModel.sideEffect.collectLatest { sideEffect ->
            currentOnSideEffect(sideEffect)
        }
    }

    ChzzkScreen(
        uiState = uiState.value,
        onEvent = viewModel::onEvent
    )
}
