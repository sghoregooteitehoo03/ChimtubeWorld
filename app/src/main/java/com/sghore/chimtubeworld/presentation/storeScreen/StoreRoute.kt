package com.sghore.chimtubeworld.presentation.storeScreen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.sghore.chimtubeworld.data.model.Goods

@Composable
fun StoreRoute(
    viewModel: StoreViewModel = hiltViewModel(),
    onGoodsClick: (List<Goods?>, Int) -> Unit
) {
    val uiState by viewModel.state.collectAsState()

    StoreScreen(
        uiState = uiState,
        onCategoryClick = viewModel::changeCategory,
        onGoodsClick = onGoodsClick
    )
}