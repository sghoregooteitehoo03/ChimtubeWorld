package com.sghore.chimtubeworld.feature.cafe.impl.cafe_screen

import androidx.paging.PagingData
import com.sghore.chimtubeworld.core.model.Channel
import com.sghore.chimtubeworld.core.model.Post
import com.sghore.chimtubeworld.core.ui.base.UiEvent
import com.sghore.chimtubeworld.core.ui.base.UiSideEffect
import com.sghore.chimtubeworld.core.ui.base.UiState
import kotlinx.coroutines.flow.Flow

internal data class CafeUiState(
    val cafeInfo: Channel? = null,
    val cafeCategoryId: Int = 0,
    val cafePosts: Flow<PagingData<Post>>? = null,
    val readHistory: Map<Int, Boolean> = emptyMap(),
    val isLoading: Boolean = false,
    val hasLoadError: Boolean = false
) : UiState

internal sealed interface CafeUiEvent : UiEvent {
    data class CategoryClicked(val categoryId: Int) : CafeUiEvent
    data class PostClicked(val post: Post) : CafeUiEvent
    data class BannerClicked(val url: String) : CafeUiEvent
    data object ChimhahaClicked : CafeUiEvent
}

internal sealed interface CafeUiSideEffect : UiSideEffect {
    data class OpenUrl(val url: String) : CafeUiSideEffect
}
