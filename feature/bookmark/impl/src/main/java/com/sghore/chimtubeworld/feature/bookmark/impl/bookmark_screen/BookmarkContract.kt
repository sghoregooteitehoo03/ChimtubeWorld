package com.sghore.chimtubeworld.feature.bookmark.impl.bookmark_screen

import com.sghore.chimtubeworld.core.model.Bookmark
import com.sghore.chimtubeworld.core.model.Video
import com.sghore.chimtubeworld.core.model.VideoSource
import com.sghore.chimtubeworld.core.ui.base.UiEvent
import com.sghore.chimtubeworld.core.ui.base.UiSideEffect
import com.sghore.chimtubeworld.core.ui.base.UiState

internal data class BookmarkUiState(
    val videoData: Video? = null,
    val videoSource: VideoSource? = VideoSource.YOUTUBE,
    val bookmarkTitle: String = "",
    val videoPosition: String = "",
    val selectedColor: Int = 0xFFFF0000.toInt(),
    val isEnable: Boolean = false,
    val isLoading: Boolean = false,
    val hasLoadError: Boolean = false,
    val isEditing: Boolean = false,
    val isOpenDialog: Boolean = false
) : UiState

internal sealed interface BookmarkUiEvent : UiEvent {
    data object BackClicked : BookmarkUiEvent
    data class TitleChanged(val title: String) : BookmarkUiEvent
    data class PositionChanged(val position: String) : BookmarkUiEvent
    data class ColorChanged(val color: Int) : BookmarkUiEvent
    data object SaveClicked : BookmarkUiEvent
    data object CopyUrlClicked : BookmarkUiEvent
    data object DeleteClicked : BookmarkUiEvent
    data object DeleteConfirmed : BookmarkUiEvent
    data object DialogDismissed : BookmarkUiEvent
}

internal enum class BookmarkChange { ADDED, EDITED, DELETED }

internal sealed interface BookmarkUiSideEffect : UiSideEffect {
    data object NavigateBack : BookmarkUiSideEffect
    data object InvalidPosition : BookmarkUiSideEffect
    data object LoadFailed : BookmarkUiSideEffect
    data class CopyUrl(val url: String) : BookmarkUiSideEffect
    data class BookmarkChanged(val bookmark: Bookmark, val change: BookmarkChange) : BookmarkUiSideEffect
}
