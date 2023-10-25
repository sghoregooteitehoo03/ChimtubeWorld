package com.sghore.chimtubeworld.presentation.storeDetailScreen

data class StoreDetailScreenState(
    val selectedImage: String = "",
    val previewImages: List<String> = emptyList(),
    val errorMsg: String = ""
)
