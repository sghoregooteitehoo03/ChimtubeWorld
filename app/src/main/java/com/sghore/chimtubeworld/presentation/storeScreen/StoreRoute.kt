package com.sghore.chimtubeworld.presentation.storeScreen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.sghore.chimtubeworld.data.model.Goods
import com.sghore.chimtubeworld.other.OpenOtherApp

@Composable
fun StoreRoute(
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