package com.sghore.chimtubeworld.feature.store.impl.store_screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.collectLatest

@Composable
internal fun StoreRoute(
    viewModel: StoreViewModel = hiltViewModel(),
    onSideEffect: (StoreUiSideEffect) -> Unit
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle()
    val currentOnSideEffect by rememberUpdatedState(onSideEffect)

    LaunchedEffect(viewModel.sideEffect) {
        viewModel.sideEffect.collectLatest { sideEffect ->
            currentOnSideEffect(sideEffect)
        }
    }

    StoreScreen(
        uiState = uiState.value,
        onEvent = viewModel::onEvent
    )
}
