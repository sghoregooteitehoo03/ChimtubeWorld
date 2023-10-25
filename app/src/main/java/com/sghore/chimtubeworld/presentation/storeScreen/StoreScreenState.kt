package com.sghore.chimtubeworld.presentation.storeScreen

import androidx.paging.PagingData
import com.sghore.chimtubeworld.data.model.Goods
import com.sghore.chimtubeworld.data.model.GoodsChannelInfo
import com.sghore.chimtubeworld.other.Contents
import kotlinx.coroutines.flow.Flow

data class StoreScreenState(
    val storeInfoList: List<GoodsChannelInfo> = listOf(),
    val selectedStoreUrl: String = "",
    val goodsList: Flow<PagingData<Goods>>? = null,
    val isLoading: Boolean = false,
    val errorMsg: String = ""
)