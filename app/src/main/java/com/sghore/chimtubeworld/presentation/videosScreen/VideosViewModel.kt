package com.sghore.chimtubeworld.presentation.videosScreen

import androidx.lifecycle.*
import androidx.paging.cachedIn
import androidx.paging.map
import com.sghore.chimtubeworld.data.model.Bookmark
import com.sghore.chimtubeworld.domain.GetVideosUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class VideosViewModel @Inject constructor(
    private val getVideosUseCase: GetVideosUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _state = MutableStateFlow(VideosScreenState())
    val state = _state.asStateFlow()

    init {
        val typeImageRes = savedStateHandle.get<Int>("typeImageRes")
        val channelId = savedStateHandle.get<String>("channelId")

        if (typeImageRes != null && channelId != null) {
            _state.update {
                VideosScreenState(
                    videos = getVideosUseCase(typeImageRes, channelId)
                        .cachedIn(viewModelScope),
                    isLoading = true
                )
            }
        }
    }

    fun setMessage(message: String) {
        _state.update {
            it.copy(
                toastMsg = message
            )
        }
    }

    // 동영상 북마크리스트의 변경사항을 적용함
    fun changeVideoBookmarks(bookmark: Bookmark) {
        _state.update {
            it.copy(
                videos = it.videos?.map { pagingData ->
                    pagingData.map { video ->
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
    }
}