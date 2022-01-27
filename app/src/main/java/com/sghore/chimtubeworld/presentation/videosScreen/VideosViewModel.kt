package com.sghore.chimtubeworld.presentation.videosScreen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.*
import androidx.paging.cachedIn
import androidx.paging.map
import com.sghore.chimtubeworld.data.model.Bookmark
import com.sghore.chimtubeworld.domain.GetVideosUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.map

class VideosViewModel @AssistedInject constructor(
    private val getVideosUseCase: GetVideosUseCase,
    @Assisted val channelId: String,
    @Assisted val typeImageRes: Int
) : ViewModel() {
    var state by mutableStateOf(VideosScreenState())
        private set

    init {
        state = VideosScreenState(
            videos = getVideosUseCase(typeImageRes, channelId)
                .cachedIn(viewModelScope),
            isLoading = true
        )
    }

    fun setMessage(message: String) {
        state = state.copy(
            toastMsg = message
        )
    }

    // 동영상 북마크리스트의 변경사항을 적용함
    fun changeVideoBookmarks(bookmark: Bookmark) {
        state = state.copy(
            videos = state.videos
                ?.map {
                    it.map { video ->
                        // 어떤 영상에 추가하거나 변경하고자 하는 북마크가 해당 영상 id와 같은지 확인
                        if (video.id == bookmark.videoId) {
                            val index = video.bookmarks.indexOf(bookmark)

                            // 북마크 리스트에 저장된 데이터가 있을 때
                            val changeBookmarks = if (index != -1) {
                                // 북마크 리스트의 아이템을 수정하는 경우
                                if (bookmark.title.isNotEmpty() && bookmark.videoPosition != 0L) {
                                    video.bookmarks.toMutableList().apply {
                                        this[index] = bookmark
                                        this.toList()
                                    }
                                } else { // 북마크 리스트의 아이템을 삭제하는 경우
                                    video.bookmarks.toMutableList().apply {
                                        this.removeAt(index)
                                        this.toList()
                                    }
                                }
                            } else { // 저장된 북마크가 없을 때
                                // 북마크 리스트에 추가함
                                video.bookmarks.toMutableList().apply {
                                    this.add(bookmark)
                                    this.toList()
                                }
                            }

                            video.copy(bookmarks = changeBookmarks)
                        } else {
                            video
                        }
                    }
                }
        )
    }

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