package com.sghore.chimtubeworld.data.model

data class Goods(
    val title: String,
    val price: Int,
    val thumbnailImage: String,
    val previewImages: List<String>,
    val url: String,
)
