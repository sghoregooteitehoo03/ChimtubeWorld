package com.sghore.chimtubeworld.presentation.storeScreen

import androidx.paging.PagingData
import com.sghore.chimtubeworld.data.model.Goods
import com.sghore.chimtubeworld.data.model.GoodsChannelInfo
import com.sghore.chimtubeworld.other.Contents
import kotlinx.coroutines.flow.Flow

data class StoreScreenState(
    val storeInfoList: List<GoodsChannelInfo> = listOf(),
    val selectedProductType: ProductType = ProductType.MarpleProduct,
    val marpleGoodsList: Flow<PagingData<Goods>>? = null,
    val naverGoodsList: Flow<PagingData<Goods>>? = null,
    val isLoading: Boolean = false,
    val errorMsg: String = ""
)

sealed interface ProductType {
    data object MarpleProduct : ProductType
    data object NaverProduct : ProductType
}