package com.sghore.chimtubeworld.presentation.webToonScreen

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sghore.chimtubeworld.data.model.Channel
import com.sghore.chimtubeworld.data.model.Resource
import com.sghore.chimtubeworld.data.repository.WebToonRepository
import com.sghore.chimtubeworld.domain.GetWebToonListUseCase
import com.sghore.chimtubeworld.presentation.twitchScreen.TwitchScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class WebToonViewModel @Inject constructor(
    private val getWebToonListUseCase: GetWebToonListUseCase
) : ViewModel() {
    private val _state = mutableStateOf(WebToonScreenState())
    val state: State<WebToonScreenState> = _state

    init {
        getWebToonList()
    }

    fun getWebToonList() {
        getWebToonListUseCase().onEach { resource ->
            when (resource) {
                is Resource.Success -> {
                    _state.value = WebToonScreenState(
                        webtoos = resource.data
                    )
                }
                is Resource.Loading -> {
                    _state.value = WebToonScreenState(
                        isLoading = true
                    )
                }
                is Resource.Error -> {
                    _state.value = WebToonScreenState(
                        errorMsg = resource.errorMsg ?: "오류"
                    )
                }
            }
        }.launchIn(viewModelScope)
    }
}