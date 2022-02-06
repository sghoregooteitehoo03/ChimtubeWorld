package com.sghore.chimtubeworld.presentation.storeScreen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.sghore.chimtubeworld.data.model.Channel
import com.sghore.chimtubeworld.data.model.Goods
import kotlinx.coroutines.flow.Flow

data class StoreScreenState(
    val storeInfoList: List<Channel> = emptyList(),
    val isLoading: Boolean = false,
    val errorMsg: String = ""
) {
    var goodsState by mutableStateOf(GoodsState())
}

data class GoodsState(
    val selectedStoreUrl: String = "",
    val goodsListFlow: Flow<List<Goods>>? = null
)