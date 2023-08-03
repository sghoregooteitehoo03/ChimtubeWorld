package com.sghore.chimtubeworld.presentation.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sghore.chimtubeworld.data.model.Bookmark
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class GlobalViewModel : ViewModel() {
    var bookmarkData: Bookmark? = null // 북마크 데이터


    private val _eventFlow = MutableSharedFlow<ActionEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    fun setEventFlow(event: ActionEvent) = viewModelScope.launch {
        _eventFlow.emit(event)
    }

    sealed class ActionEvent {
        object ShowHelp : ActionEvent()
        object DeleteBookmark : ActionEvent()
        object CopyVideoUrl : ActionEvent()
    }
}