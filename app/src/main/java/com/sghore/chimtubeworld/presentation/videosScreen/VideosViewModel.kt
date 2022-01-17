package com.sghore.chimtubeworld.presentation.videosScreen

import androidx.lifecycle.*
import androidx.paging.cachedIn
import com.sghore.chimtubeworld.data.repository.VideosRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

class VideosViewModel @AssistedInject constructor(
    private val repository: VideosRepository,
    @Assisted val channelId: String,
    @Assisted val typeImageRes: Int
) : ViewModel() {
    // 로딩 여부
    val isLoading = MutableLiveData(true)

    // 영상 리스트
    val vidoes = repository.getVideos(
        channelId = channelId,
        typeImageRes = typeImageRes
    ).cachedIn(viewModelScope).asLiveData(viewModelScope.coroutineContext)

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