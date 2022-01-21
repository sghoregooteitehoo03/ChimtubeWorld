package com.sghore.chimtubeworld.presentation.storeScreen

import com.sghore.chimtubeworld.data.model.Channel
import com.sghore.chimtubeworld.data.model.Goods
import kotlinx.coroutines.flow.Flow

data class StoreScreenState(
    val storeInfoList: List<Channel> = emptyList(),
    val goodsList: Flow<List<Goods>>? = null,
    val selectedStoreUrl: String = "",
    val isLoading: Boolean = false,
    val errorMsg: String = ""
)
