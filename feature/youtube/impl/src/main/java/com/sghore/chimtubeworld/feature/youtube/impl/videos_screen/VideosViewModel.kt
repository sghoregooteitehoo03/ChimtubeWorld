package com.sghore.chimtubeworld.feature.youtube.impl.videos_screen

import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import androidx.paging.map
import com.sghore.chimtubeworld.core.common.Constants
import com.sghore.chimtubeworld.core.domain.repository.YoutubeRepository
import com.sghore.chimtubeworld.core.model.Bookmark
import com.sghore.chimtubeworld.core.model.Video
import com.sghore.chimtubeworld.core.model.VideoSource
import com.sghore.chimtubeworld.core.ui.base.BaseViewModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = VideosViewModel.Factory::class)
internal class VideosViewModel @AssistedInject constructor(
    private val repository: YoutubeRepository,
    @Assisted("playlistId") private val playlistId: String,
    @Assisted("title") title: String,
    @Assisted private val source: VideoSource
) : BaseViewModel<VideosUiState, VideosUiEvent, VideosUiSideEffect>(VideosUiState(title = title)) {

    init {
        val videos = repository.getVideos(channelId = playlistId.split("|")[0]).cachedIn(viewModelScope)

        reduce { copy(videos = videos) }
    }

    override fun onEvent(event: VideosUiEvent) {
        when (event) {
            VideosUiEvent.BackClicked -> postSideEffect(VideosUiSideEffect.NavigateBack)
            VideosUiEvent.HelpClicked -> postSideEffect(VideosUiSideEffect.ShowHelp)
            VideosUiEvent.DialogDismissed -> reduce { copy(selectedVideo = null) }
            is VideosUiEvent.VideoClicked -> {
                if (event.video.bookmarks.isEmpty()) {
                    openVideo(event.video, event.video.url)
                } else {
                    reduce { copy(selectedVideo = event.video) }
                }
            }
            is VideosUiEvent.BookmarkClicked -> postSideEffect(
                VideosUiSideEffect.NavigateToEditBookmark(event.video, event.position, source)
            )
            is VideosUiEvent.PositionSelected -> {
                val video = currentState.selectedVideo ?: return
                val seconds = "${(event.videoPosition + 32400000) / 1000}s"
                val separator = if (video.url.contains("?")) "&" else "?"

                reduce { copy(selectedVideo = null) }
                openVideo(video, "${video.url}${separator}t=$seconds")
            }
            is VideosUiEvent.BookmarkResultReceived -> changeVideoBookmarks(event.bookmark)
        }
    }

    private fun openVideo(video: Video, url: String) {
        val packageName = if (source == VideoSource.YOUTUBE) {
            Constants.YOUTUBE_PACKAGE_NAME
        } else {
            Constants.TWITCH_VIDEO_PACKAGE_NAME + video.id
        }

        postSideEffect(VideosUiSideEffect.OpenVideo(packageName, url, source))
    }

    // 동영상 북마크리스트의 변경사항을 적용함
    private fun changeVideoBookmarks(bookmark: Bookmark) = viewModelScope.launch {
        reduce {
            copy(
                videos = videos?.map { pagingData ->
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

                            video.copy(bookmarks = changeBookmarks.sortedBy { it.videoPosition })
                        } else {
                            video
                        }
                    }
                }?.cachedIn(viewModelScope)
            )
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted("playlistId") playlistId: String,
            @Assisted("title") title: String,
            source: VideoSource
        ): VideosViewModel
    }
}
