package com.sghore.chimtubeworld.data.retrofit.dto.naver_storeAPI

data class SimpleProductDTO(
    val id: String,
    val name: String,
    val salePrice: Int,
    val representativeImageUrl: String,
    val optionalImageUrls: List<String>
)
