package com.sghore.chimtubeworld.viewmodel.videosFrag

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.sghore.chimtubeworld.R
import com.sghore.chimtubeworld.other.Contents
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

class VideosViewModel @AssistedInject constructor(
    private val repository: VideosRepository,
    @Assisted val channelId: String,
    @Assisted val typeImageRes: Int
) : ViewModel() {
    val vidoes = repository.getVideos(
        channelId = channelId,
        typeImageRes = typeImageRes
    ).asLiveData(viewModelScope.coroutineContext)

    @dagger.assisted.AssistedFactory
    interface AssistedFactory {
        fun create(channelId: String, typeImageRes: Int): VideosViewModel
    }

    companion object {
        fun provideFactory(
            assistedFactory: AssistedFactory,
            channelId: String,
            typeImageRes: Int
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {

            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return assistedFactory.create(channelId, typeImageRes) as T
            }
        }
    }
}