package com.sghore.chimtubeworld.feature.youtube.impl.youtube_screen

import com.sghore.chimtubeworld.core.model.Channel
import com.sghore.chimtubeworld.core.ui.base.UiEvent
import com.sghore.chimtubeworld.core.ui.base.UiSideEffect
import com.sghore.chimtubeworld.core.ui.base.UiState

internal data class YoutubeUiState(
    val channels: List<Channel?> = emptyList(),
    val isLoading: Boolean = false,
    val hasLoadError: Boolean = false
) : UiState

internal sealed interface YoutubeUiEvent : UiEvent {
    data class ChannelClicked(val channel: Channel) : YoutubeUiEvent
}

internal sealed interface YoutubeUiSideEffect : UiSideEffect {
    data class NavigateToPlaylists(val channel: Channel) : YoutubeUiSideEffect
}
