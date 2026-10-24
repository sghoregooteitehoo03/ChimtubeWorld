package com.sghore.chimtubeworld.feature.store.impl.store_screen

import com.sghore.chimtubeworld.core.model.GoodsChannelInfo
import com.sghore.chimtubeworld.core.ui.base.UiEvent
import com.sghore.chimtubeworld.core.ui.base.UiSideEffect
import com.sghore.chimtubeworld.core.ui.base.UiState
import com.sghore.chimtubeworld.feature.store.impl.R

internal data class StoreUiState(
    val storeInfoList: List<GoodsChannelInfo> = listOf(
        GoodsChannelInfo(
            channelImage = R.drawable.marple,
            channelName = "침착맨",
            url = "https://smartstore.naver.com/chim_chungja/best?cp=1"
        ),
        GoodsChannelInfo(
            channelImage = R.drawable.naver,
            channelName = "얼렁뚱땅 상점",
            url = "https://uldd.net/collabochim"
        )
    )
) : UiState

internal sealed interface StoreUiEvent : UiEvent {
    data class StoreClicked(val storeInfo: GoodsChannelInfo) : StoreUiEvent
}

internal sealed interface StoreUiSideEffect : UiSideEffect {
    data class OpenUrl(val url: String) : StoreUiSideEffect
}
