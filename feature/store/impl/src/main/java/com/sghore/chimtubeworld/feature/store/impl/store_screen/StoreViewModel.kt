package com.sghore.chimtubeworld.feature.store.impl.store_screen

import com.sghore.chimtubeworld.core.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class StoreViewModel @Inject constructor() :
    BaseViewModel<StoreUiState, StoreUiEvent, StoreUiSideEffect>(StoreUiState()) {

    override fun onEvent(event: StoreUiEvent) {
        when (event) {
            is StoreUiEvent.StoreClicked -> {
                postSideEffect(StoreUiSideEffect.OpenUrl(event.storeInfo.url))
            }
        }
    }
}
