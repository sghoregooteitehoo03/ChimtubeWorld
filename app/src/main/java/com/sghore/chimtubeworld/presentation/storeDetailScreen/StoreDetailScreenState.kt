package com.sghore.chimtubeworld.presentation.storeDetailScreen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

data class StoreDetailScreenState(
    val isLoading: Boolean = false,
    val errorMsg: String = ""
) {
    var selectedImageState by mutableStateOf(SelectedImageState())
}

data class SelectedImageState(
    val selectedImage: String = "",
    val previewImages: List<String> = emptyList(),
)
