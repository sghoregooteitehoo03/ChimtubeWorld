package com.sghore.chimtubeworld.data.retrofit.dto.marpleAPI

import com.google.gson.annotations.SerializedName

data class ProductsDTO(
    val id: Int,
    val name: String,
    val price: Int,
    val profit: Int,
    @SerializedName("_")
    val productInfo: ProductInfoDTO,
    val thumbnails: ThumbnailDTO
)