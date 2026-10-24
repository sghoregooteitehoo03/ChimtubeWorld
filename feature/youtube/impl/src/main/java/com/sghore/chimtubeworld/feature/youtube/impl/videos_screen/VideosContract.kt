package com.sghore.chimtubeworld.feature.youtube.impl.videos_screen

import androidx.paging.PagingData
import com.sghore.chimtubeworld.core.model.Bookmark
import com.sghore.chimtubeworld.core.model.Video
import com.sghore.chimtubeworld.core.model.VideoSource
import com.sghore.chimtubeworld.core.ui.base.UiEvent
import com.sghore.chimtubeworld.core.ui.base.UiSideEffect
import com.sghore.chimtubeworld.core.ui.base.UiState
import kotlinx.coroutines.flow.Flow

internal data class VideosUiState(
    val title: String = "",
    val videos: Flow<PagingData<Video>>? = null,
    val selectedVideo: Video? = null
) : UiState

internal sealed interface VideosUiEvent : UiEvent {
    data object BackClicked : VideosUiEvent
    data object HelpClicked : VideosUiEvent
    data object DialogDismissed : VideosUiEvent
    data class VideoClicked(val video: Video) : VideosUiEvent
    data class BookmarkClicked(val video: Video, val position: Int) : VideosUiEvent
    data class PositionSelected(val videoPosition: Long) : VideosUiEvent
    data class BookmarkResultReceived(val bookmark: Bookmark) : VideosUiEvent
}

internal sealed interface VideosUiSideEffect : UiSideEffect {
    data object NavigateBack : VideosUiSideEffect
    data object ShowHelp : VideosUiSideEffect
    data class NavigateToEditBookmark(val video: Video, val position: Int, val source: VideoSource) : VideosUiSideEffect
    data class OpenVideo(val packageName: String, val url: String, val source: VideoSource) : VideosUiSideEffect
}
