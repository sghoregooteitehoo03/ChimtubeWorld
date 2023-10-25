package com.sghore.chimtubeworld.data.retrofit.dto.naverAPI

import com.google.gson.annotations.SerializedName

data class SimpleProductDTO(
    val id: String,
    val name: String,
    val salePrice: Int,
    val representativeImageUrl: String,
    val optionalImageUrls: List<String>
)
