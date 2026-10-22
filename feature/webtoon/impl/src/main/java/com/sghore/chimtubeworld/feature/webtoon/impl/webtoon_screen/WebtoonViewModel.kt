package com.sghore.chimtubeworld.feature.webtoon.impl.webtoon_screen

import com.sghore.chimtubeworld.core.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class WebtoonViewModel @Inject constructor() :
    BaseViewModel<WebtoonUiState, WebtoonUiEvent, WebtoonUiSideEffect>(WebtoonUiState()) {

    override fun onEvent(event: WebtoonUiEvent) {
        when (event) {
            is WebtoonUiEvent.WebtoonClicked -> {
                postSideEffect(
                    WebtoonUiSideEffect.OpenWebtoon(event.webtoon.id, event.webtoon.url)
                )
            }
        }
    }
}
