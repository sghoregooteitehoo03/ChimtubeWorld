package com.sghore.chimtubeworld.presentation.storeScreen

import com.sghore.chimtubeworld.data.model.Goods
import com.sghore.chimtubeworld.data.model.GoodsElement

data class StoreScreenState(
    val storeInfoList: List<GoodsElement> = emptyList(),
    val selectedStoreUrl: String = "",
    val goodsList: List<Goods> = emptyList(),
    val isLoading: Boolean = false,
    val errorMsg: String = ""
)