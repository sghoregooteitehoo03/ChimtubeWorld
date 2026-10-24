package com.sghore.chimtubeworld.core.ui.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/*
 * feature ViewModel이 공통으로 사용하는 MVI 기반 클래스다.
 * UI 상태와 이벤트 흐름을 관리하며, 비즈니스 규칙은 domain/usecase 또는 feature ViewModel에 둔다.
 */
abstract class BaseViewModel<S : UiState, E : UiEvent, SE : UiSideEffect>(
    initialState: S
) : ViewModel() {
    private val _uiState = MutableStateFlow(initialState)
    val uiState: StateFlow<S> = _uiState.asStateFlow()

    private val _sideEffect = MutableSharedFlow<SE>(extraBufferCapacity = 1)
    val sideEffect: SharedFlow<SE> = _sideEffect.asSharedFlow()

    protected val currentState: S
        get() = _uiState.value

    abstract fun onEvent(event: E)

    protected fun reduce(reducer: S.() -> S) {
        _uiState.update(reducer)
    }

    protected fun postSideEffect(sideEffect: SE) {
        viewModelScope.launch {
            _sideEffect.emit(sideEffect)
        }
    }
}
