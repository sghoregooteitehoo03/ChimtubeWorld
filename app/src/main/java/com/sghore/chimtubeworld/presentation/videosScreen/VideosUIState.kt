package com.sghore.chimtubeworld.presentation.videosScreen

import androidx.paging.PagingData
import com.sghore.chimtubeworld.data.model.Video
import kotlinx.coroutines.flow.Flow

data class VideosUIState(
    val videosScreenStates: List<VideosScreenState> = emptyList(),
    val tabIndex: Int = 0,
    val isDialogOpen: Boolean = false
)

data class VideosScreenState(
    val videos: Flow<PagingData<Video>>? = null,
    val playlistName: String = ""
)