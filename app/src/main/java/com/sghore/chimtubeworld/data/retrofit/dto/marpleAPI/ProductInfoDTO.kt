package com.sghore.chimtubeworld.data.retrofit.dto.marpleAPI

import com.google.gson.annotations.SerializedName

data class ProductInfoDTO(
    @SerializedName("base_product")
    val baseProduct: BaseProductDTO
)