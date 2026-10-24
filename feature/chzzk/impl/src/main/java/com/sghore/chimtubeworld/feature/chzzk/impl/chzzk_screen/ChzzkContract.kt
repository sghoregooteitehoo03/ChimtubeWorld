package com.sghore.chimtubeworld.feature.chzzk.impl.chzzk_screen

import com.sghore.chimtubeworld.core.model.Channel
import com.sghore.chimtubeworld.core.ui.base.UiEvent
import com.sghore.chimtubeworld.core.ui.base.UiSideEffect
import com.sghore.chimtubeworld.core.ui.base.UiState

internal data class ChzzkUiState(
    val mainChannelInfo: Channel? = null,
    val channels: List<Channel> = emptyList(),
    val isLoading: Boolean = false,
    val isDialogOpen: Boolean = false,
    val hasLoadError: Boolean = false
) : UiState

internal sealed interface ChzzkUiEvent : UiEvent {
    data object MainChannelClicked : ChzzkUiEvent
    data object DialogDismissed : ChzzkUiEvent
    data object YoutubeClicked : ChzzkUiEvent
    data object ChzzkClicked : ChzzkUiEvent
    data class CrewChannelClicked(val channel: Channel) : ChzzkUiEvent
}

internal sealed interface ChzzkUiSideEffect : UiSideEffect {
    data class OpenYoutube(val url: String) : ChzzkUiSideEffect
    data class OpenUrl(val url: String) : ChzzkUiSideEffect
}
