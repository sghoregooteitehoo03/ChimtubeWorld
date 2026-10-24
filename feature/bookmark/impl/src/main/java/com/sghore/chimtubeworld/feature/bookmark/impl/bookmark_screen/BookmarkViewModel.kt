package com.sghore.chimtubeworld.feature.bookmark.impl.bookmark_screen

import androidx.lifecycle.viewModelScope
import com.sghore.chimtubeworld.core.common.suspendRunCatching
import com.sghore.chimtubeworld.core.domain.repository.BookmarkRepository
import com.sghore.chimtubeworld.core.domain.repository.YoutubeRepository
import com.sghore.chimtubeworld.core.model.Bookmark
import com.sghore.chimtubeworld.core.model.Video
import com.sghore.chimtubeworld.core.model.VideoSource
import com.sghore.chimtubeworld.core.ui.base.BaseViewModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import java.text.SimpleDateFormat
import java.util.Locale
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = BookmarkViewModel.Factory::class)
internal class BookmarkViewModel @AssistedInject constructor(
    private val youtubeRepository: YoutubeRepository,
    private val repository: BookmarkRepository,
    @Assisted private val videoData: Video?,
    @Assisted private val pos: Int,
    @Assisted private val source: VideoSource?,
    @Assisted private val videoUrl: String?
) : BaseViewModel<BookmarkUiState, BookmarkUiEvent, BookmarkUiSideEffect>(BookmarkUiState(isLoading = true)) {

    private val selectedBookmark: Bookmark? = if (videoData != null && pos != -1 && source != null) {
        videoData.bookmarks[pos]
    } else {
        null
    }

    init {
        if (selectedBookmark != null) {
            reduce {
                BookmarkUiState(
                    // Use the assisted argument, not the reduce receiver's initially null videoData.
                    videoData = this@BookmarkViewModel.videoData,
                    videoSource = source,
                    bookmarkTitle = selectedBookmark.title,
                    videoPosition = getDateStrFromPosition(selectedBookmark.videoPosition),
                    selectedColor = selectedBookmark.color,
                    isEnable = true,
                    isEditing = true
                )
            }
        } else if (videoUrl != null) {
            loadVideo(videoUrl)
        }
    }

    override fun onEvent(event: BookmarkUiEvent) {
        when (event) {
            BookmarkUiEvent.BackClicked -> postSideEffect(BookmarkUiSideEffect.NavigateBack)
            is BookmarkUiEvent.TitleChanged -> {
                if (event.title.length <= 10) {
                    reduce { copy(bookmarkTitle = event.title, isEnable = event.title.isNotEmpty() && videoPosition.isNotEmpty()) }
                }
            }
            is BookmarkUiEvent.PositionChanged -> {
                if (event.position.length <= 8) {
                    reduce { copy(videoPosition = event.position, isEnable = bookmarkTitle.isNotEmpty() && event.position.isNotEmpty()) }
                }
            }
            is BookmarkUiEvent.ColorChanged -> reduce { copy(selectedColor = event.color) }
            BookmarkUiEvent.SaveClicked -> saveBookmark()
            BookmarkUiEvent.CopyUrlClicked -> copyUrl()
            BookmarkUiEvent.DeleteClicked -> if (selectedBookmark != null) reduce { copy(isOpenDialog = true) }
            BookmarkUiEvent.DialogDismissed -> reduce { copy(isOpenDialog = false) }
            BookmarkUiEvent.DeleteConfirmed -> {
                if (currentState.isOpenDialog) {
                    reduce { copy(isOpenDialog = false) }
                    deleteBookmark()
                }
            }
        }
    }

    private fun loadVideo(url: String) {
        viewModelScope.launch {
            reduce { copy(isLoading = true, hasLoadError = false) }

            suspendRunCatching {
                youtubeRepository.getVideo(url)
            }.onSuccess { video ->
                reduce { BookmarkUiState(videoData = video, videoSource = null) }
            }.onFailure { error ->
                if (error !is Exception) throw error
                error.printStackTrace()

                reduce { BookmarkUiState(hasLoadError = true) }
                postSideEffect(BookmarkUiSideEffect.LoadFailed)
            }
        }
    }

    private fun saveBookmark() {
        val state = currentState
        if (!state.isEnable) return
        val video = state.videoData ?: return
        val position = getDateFromPosition(state.videoPosition)
        if (!checkVideoPosition(position, video.duration)) {
            postSideEffect(BookmarkUiSideEffect.InvalidPosition)
            return
        }

        viewModelScope.launch {
            val bookmark = Bookmark(
                id = selectedBookmark?.id,
                videoId = video.id,
                title = state.bookmarkTitle,
                videoPosition = position!!,
                color = state.selectedColor
            )
            repository.addBookmark(bookmark)
            val result = if (selectedBookmark == null) {
                bookmark.copy(id = repository.getItemId(bookmark))
            } else {
                bookmark
            }

            postSideEffect(BookmarkUiSideEffect.BookmarkChanged(
                result, if (selectedBookmark == null) BookmarkChange.ADDED else BookmarkChange.EDITED
            ))
        }
    }

    private fun deleteBookmark() {
        val bookmark = selectedBookmark ?: return
        viewModelScope.launch {
            repository.deleteBookmark(bookmark)
            // Preserve the result sentinel consumed by Videos.
            postSideEffect(BookmarkUiSideEffect.BookmarkChanged(
                bookmark.copy(title = "", videoPosition = 0L), BookmarkChange.DELETED
            ))
        }
    }

    private fun copyUrl() {
        val bookmark = selectedBookmark ?: return
        val time = getSecondsFromPosition(bookmark.videoPosition)
        val url = when (currentState.videoSource) {
            VideoSource.YOUTUBE -> "${currentState.videoData!!.url}&t=$time"
            VideoSource.TWITCH -> "${currentState.videoData!!.url}?t=$time"
            else -> ""
        }

        postSideEffect(BookmarkUiSideEffect.CopyUrl(url))
    }

    // 영상 범위 확인
    private fun checkVideoPosition(positionTime: Long?, duration: Long): Boolean {
        return if (positionTime == null) {
            false
        } else {
            // 0[s] <= position <= Video Duration[s]
            -32400000 <= positionTime && positionTime <= duration
        }
    }

    // 입력한 시간을 시:분:초 데이터로 파싱하여 Long 데이터로 반환
    private fun getDateFromPosition(videoPosition: String): Long? {
        val splitSize = videoPosition.split(":").size
        val dateFormat = if (splitSize == 3) {
            SimpleDateFormat(
                "hh:mm:ss",
                Locale.KOREA
            )
        } else if (splitSize == 2) {
            SimpleDateFormat(
                "mm:ss",
                Locale.KOREA
            )
        } else {
            return null
        }

        return try {
            dateFormat.parse(videoPosition).time
        } catch (e: Exception) {
            return null
        }
    }

    // 시간 데이터를 시:분:초 String으로 변환하여 반환
    private fun getDateStrFromPosition(videoPosition: Long): String {
        val dateFormat = if (videoPosition >= -28800000) { // 01:00:00
            SimpleDateFormat(
                "hh:mm:ss",
                Locale.KOREA
            )
        } else {
            SimpleDateFormat(
                "mm:ss",
                Locale.KOREA
            )
        }

        return dateFormat.format(videoPosition)
    }

    // 시간 데이터를 초로 변환
    private fun getSecondsFromPosition(videoPosition: Long) =
        "${(videoPosition + 32400000) / 1000}s"

    @AssistedFactory
    interface Factory {
        fun create(videoData: Video?, pos: Int, source: VideoSource?, videoUrl: String?): BookmarkViewModel
    }
}
