package com.sghore.chimtubeworld.presentation.storeScreen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.sghore.chimtubeworld.data.model.Goods

// TODO
//  . 굿즈 리스트 최신화
//  . 굿즈 다이얼로그 재디자인
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