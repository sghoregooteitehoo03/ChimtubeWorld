package com.sghore.chimtubeworld.presentation.webToonScreen

import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
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
    var state by mutableStateOf(WebToonScreenState())
        private set

    init {
        getWebToonList()
    }

    fun getWebToonList() {
        getWebToonListUseCase().onEach { resource ->
            state = when (resource) {
                is Resource.Success -> {
                    WebToonScreenState(
                        webtoos = resource.data
                    )
                }
                is Resource.Loading -> {
                    WebToonScreenState(
                        isLoading = true
                    )
                }
                is Resource.Error -> {
                    WebToonScreenState(
                        errorMsg = resource.errorMsg ?: "오류"
                    )
                }
            }
        }.launchIn(viewModelScope)
    }
}