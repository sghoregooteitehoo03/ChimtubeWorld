package com.sghore.chimtubeworld.presentation.storeDetailScreen

data class StoreDetailScreenState(
    val previewImages: List<String> = emptyList(),
    val isLoading: Boolean = false,
    val selectedImage: String = "",
    val errorMsg: String = ""
)
