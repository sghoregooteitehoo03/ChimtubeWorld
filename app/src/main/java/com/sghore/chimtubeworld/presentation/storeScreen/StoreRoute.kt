package com.sghore.chimtubeworld.presentation.storeScreen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.sghore.chimtubeworld.presentation.ui.GlobalViewModel
import kotlinx.coroutines.flow.collect

@Composable
fun StoreRoute(
    viewModel: StoreViewModel,
    gViewModel: GlobalViewModel
) {
    val uiState by viewModel.state.collectAsState()
    LaunchedEffect(key1 = uiState.goodsListFlow) {
        uiState.goodsListFlow?.collect {
            viewModel.setGoodsList(it)
        }
    }

    StoreScreen(
        uiState = uiState,
        onCategoryClick = viewModel::changeCategory,
        onGoodsClick = { goodsList, goodsIndex ->
            gViewModel.showGoodsList.value = goodsList
            gViewModel.selectedGoodsPos.value = goodsIndex
        }
    )
}