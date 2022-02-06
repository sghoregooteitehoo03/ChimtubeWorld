package com.sghore.chimtubeworld.presentation.storeScreen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.sghore.chimtubeworld.data.model.Goods
import com.sghore.chimtubeworld.presentation.ui.GlobalViewModel
import kotlinx.coroutines.flow.collect

@Composable
fun StoreRoute(
    viewModel: StoreViewModel = hiltViewModel(),
    onGoodsClick: (List<Goods>, Int) -> Unit
) {
    val uiState by viewModel.state.collectAsState()

    StoreScreen(
        uiState = uiState,
        onCategoryClick = viewModel::changeCategory,
        onGoodsClick = onGoodsClick
    )
}