package com.sghore.chimtubeworld.presentation.videosScreen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.paging.PagingData
import com.sghore.chimtubeworld.data.model.Video
import kotlinx.coroutines.flow.Flow

data class VideosScreenState(
    val videos: Flow<PagingData<Video>>? = null,
    val isDialogOpen: Boolean = false
) {
    var toastMsg by mutableStateOf("")
}
