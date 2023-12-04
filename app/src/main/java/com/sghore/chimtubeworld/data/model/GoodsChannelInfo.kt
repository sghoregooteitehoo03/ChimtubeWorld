package com.sghore.chimtubeworld.data.model

import com.sghore.chimtubeworld.presentation.storeScreen.ProductType

data class GoodsChannelInfo(
    val channelImage: Int,
    val channelName: String,
    val baseUrl: String,
    val productType: ProductType
)
