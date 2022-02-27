package com.sghore.chimtubeworld.presentation.videosScreen

import androidx.lifecycle.*
import androidx.paging.cachedIn
import androidx.paging.map
import com.sghore.chimtubeworld.data.model.Bookmark
import com.sghore.chimtubeworld.data.model.Video
import com.sghore.chimtubeworld.domain.GetVideosUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VideosViewModel @Inject constructor(
    private val getVideosUseCase: GetVideosUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _state = MutableStateFlow(VideosUIState())
    val state = _state.asStateFlow()

    // 다이얼로그에 사용할 데이터
    var packageName = ""
        private set
    var video: Video? = null
        private set

    init {
        val typeImageRes = savedStateHandle.get<Int>("typeImageRes")
        val playlistId = savedStateHandle.get<String>("playlistId")
        val playlistName = savedStateHandle.get<String>("playlistName")

        if (typeImageRes != null && playlistId != null && playlistName != null) {
            val playlistIds = playlistId.split("|") // 플레이리스  아이디 리스트
            val playlistNames = playlistName.split("|") // 플레이리스트 이름 리스트

            _state.update {
                VideosUIState(
                    videosScreenStates = playlistIds.mapIndexed { index, id ->
                        VideosScreenState(
                            videos = getVideosUseCase(typeImageRes, id)
                                .cachedIn(viewModelScope),
                            playlistName = playlistNames[index]
                        )
                    }
                )
            }
        }
    }

    // 탭 위치 수정
    fun changeTabIndex(index: Int) {
        _state.update {
            it.copy(
                tabIndex = index
            )
        }
    }

    // 다이얼로그 Open 여부 설정
    fun setDialogOpen(
        _packageName: String,
        _video: Video?,
        isOpen: Boolean
    ) {
        this.packageName = _packageName
        this.video = _video

        _state.update {
            it.copy(
                isDialogOpen = isOpen
            )
        }
    }

    // 동영상 북마크리스트의 변경사항을 적용함
    fun changeVideoBookmarks(bookmark: Bookmark) = viewModelScope.launch {
        _state.update { videoUIState ->
            videoUIState.copy(
                videosScreenStates = videoUIState.videosScreenStates.mapIndexed { index, videosScreenState ->
                    if (index == videoUIState.tabIndex) {
                        // 현재 선택된 탭에 존재하는 동영상의 정보를 수정함
                        videosScreenState.copy(
                            videos = videosScreenState.videos?.map { pagingData ->
                                pagingData.map { video ->
                                    // 어떤 영상에 추가하거나 변경하고자 하는 북마크가 해당 영상 id와 같은지 확인
                                    if (video.id == bookmark.videoId) {
                                        val bookmarkIndex = video.bookmarks.indexOf(bookmark)

                                        // 북마크 리스트에 저장된 데이터가 있을 때
                                        val changeBookmarks = if (bookmarkIndex != -1) {
                                            // 북마크 리스트의 아이템을 수정하는 경우
                                            if (bookmark.title.isNotEmpty() && bookmark.videoPosition != 0L) {
                                                video.bookmarks.toMutableList().apply {
                                                    this[bookmarkIndex] = bookmark
                                                    this.toList()
                                                }
                                            } else { // 북마크 리스트의 아이템을 삭제하는 경우
                                                video.bookmarks.toMutableList().apply {
                                                    this.removeAt(bookmarkIndex)
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
                    } else {
                        videosScreenState
                    }
                }
            )
        }
    }
}