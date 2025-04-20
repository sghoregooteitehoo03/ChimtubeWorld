package com.sghore.chimtubeworld.presentation.storeScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.sghore.chimtubeworld.R
import com.sghore.chimtubeworld.data.model.GoodsChannelInfo
import com.sghore.chimtubeworld.other.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class StoreViewModel @Inject constructor(

) : ViewModel() {
    private val _state = MutableStateFlow(StoreScreenState(isLoading = true))
    val state: StateFlow<StoreScreenState> = _state
}