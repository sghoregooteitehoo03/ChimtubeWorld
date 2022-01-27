package com.sghore.chimtubeworld.presentation.videosScreen

import androidx.paging.PagingData
import com.sghore.chimtubeworld.data.model.Video
import kotlinx.coroutines.flow.Flow

data class VideosScreenState(
    val videos: Flow<PagingData<Video>>? = null,
    val isLoading: Boolean = false,
    val toastMsg: String = ""
)
