package com.sghore.chimtubeworld.presentation.storeDetailScreen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import com.sghore.chimtubeworld.other.OpenOtherApp

@Composable
fun StoreDetailRoute(
    viewModel: StoreDetailViewModel
) {
    val uiState by viewModel.state.collectAsState()
    val context = LocalContext.current

    StoreDetailScreen(
        uiState = uiState,
        goods = viewModel.goods,
        onPreviewImageClick = viewModel::selectPreviewImage,
        onActionClick = { url ->
            OpenOtherApp(context = context)
                .openCustomTabs(url)
        }
    )
}