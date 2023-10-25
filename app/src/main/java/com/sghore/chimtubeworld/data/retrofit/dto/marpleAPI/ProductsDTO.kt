package com.sghore.chimtubeworld.data.retrofit.dto.marpleAPI

import com.google.gson.annotations.SerializedName

data class ProductsDTO(
    val id: Int,
    val price: Int,
    @SerializedName("_")
    val productInfo: ProductInfoDTO,
    val thumbnails: ThumbnailDTO
)