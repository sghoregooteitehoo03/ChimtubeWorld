package com.sghore.chimtubeworld.feature.store.impl.store_screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.sghore.chimtubeworld.core.ui.OpenOtherApp

@Composable
internal fun StoreRoute(
    viewModel: StoreViewModel = hiltViewModel()
) {
    val uiState by viewModel.state.collectAsState()
    val context = LocalContext.current

    StoreScreen(
        uiState = uiState,
        onClickGoods = {
            OpenOtherApp(context = context).openCustomTabs(it.url)
        }
    )
}
