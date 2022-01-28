package com.sghore.chimtubeworld.presentation.webToonScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sghore.chimtubeworld.data.model.Resource
import com.sghore.chimtubeworld.domain.GetWebToonListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class WebToonViewModel @Inject constructor(
    private val getWebToonListUseCase: GetWebToonListUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(WebToonScreenState(isLoading = true))
    val state: StateFlow<WebToonScreenState> = _state

    init {
        getWebToonList()
    }

    fun getWebToonList() {
        getWebToonListUseCase().onEach { resource ->
            when (resource) {
                is Resource.Success -> {
                    _state.update {
                        WebToonScreenState(
                            webtoos = resource.data
                        )
                    }
                }
                is Resource.Loading -> {
                    _state.update {
                        WebToonScreenState(
                            isLoading = true
                        )
                    }
                }
                is Resource.Error -> {
                    _state.update {
                        WebToonScreenState(
                            errorMsg = resource.errorMsg ?: "오류"
                        )
                    }
                }
            }
        }.launchIn(viewModelScope)
    }
}