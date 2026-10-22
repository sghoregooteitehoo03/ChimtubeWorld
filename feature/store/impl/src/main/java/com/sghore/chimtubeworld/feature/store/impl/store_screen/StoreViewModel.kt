package com.sghore.chimtubeworld.feature.store.impl.store_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.sghore.chimtubeworld.feature.store.impl.R
import com.sghore.chimtubeworld.core.model.GoodsChannelInfo
import com.sghore.chimtubeworld.core.common.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
internal class StoreViewModel @Inject constructor(

) : ViewModel() {
    private val _state = MutableStateFlow(StoreScreenState(isLoading = true))
    val state: StateFlow<StoreScreenState> = _state
}