package com.sghore.chimtubeworld.presentation.bookmarkScreen

import androidx.lifecycle.*
import com.google.gson.Gson
import com.sghore.chimtubeworld.R
import com.sghore.chimtubeworld.data.model.Bookmark
import com.sghore.chimtubeworld.data.model.Resource
import com.sghore.chimtubeworld.data.model.Video
import com.sghore.chimtubeworld.data.repository.BookmarkRepository
import com.sghore.chimtubeworld.domain.GetTwitchVideoUseCase
import com.sghore.chimtubeworld.domain.GetYoutubeVideoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class BookmarkViewModel @Inject constructor(
    private val getYoutubeVideoUseCase: GetYoutubeVideoUseCase,
    private val getTwitchVideoUseCase: GetTwitchVideoUseCase,
    private val repository: BookmarkRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    sealed class BookmarkEvent {
        data class ShowToastMessage(val message: String) : BookmarkEvent()
        data class ChangeBookmark(val message: String, val bookmark: Bookmark) : BookmarkEvent()
    }

    private val baseYoutubeUrl = "youtu.be"
    private val baseTwitchUrl = "www.twitch.tv"

    private val _state = MutableStateFlow(BookmarkScreenState(isLoading = true))
    private val _event = MutableSharedFlow<BookmarkEvent>()

    val state = _state.asStateFlow()
    val event = _event.asSharedFlow()

    var selectedBookmark: Bookmark? = null
        private set

    init {
        // 영상 데이터
        val videoData = Gson().fromJson(
            savedStateHandle.get<String>("video"),
            Video::class.java
        )
        val pos = savedStateHandle.get<Int>("pos") ?: -1 // 북마크 위치
        val typeImageRes = savedStateHandle.get<Int>("typeImageRes") ?: -1 // 유튜브 or 트위치 이미지
        val videoUrl = savedStateHandle.get<String>("url") // 영상 url

        if (videoData != null && pos != -1 && typeImageRes != -1) {
            // 북마크 수정
            selectedBookmark = videoData.bookmarks[pos]
            initValue(videoData, selectedBookmark!!, typeImageRes)
        } else if (videoUrl != null) {
            // 북마크 생성
            getVideoData(videoUrl)
        }
    }

    // 넘겨온 영상 url을 통해 영상 정보를 가져옴
    fun getVideoData(videoUrl: String) {
        val baseUrl = getBaseUrl(videoUrl)
        var videoTypeImage = -1

        val videoFlow = if (baseUrl == baseYoutubeUrl) {
            videoTypeImage = R.drawable.youtube
            getYoutubeVideoUseCase(videoUrl, baseUrl)
        } else {
            videoTypeImage = R.drawable.twitch
            getTwitchVideoUseCase(videoUrl, baseUrl)
        }

        videoFlow.onEach { resource ->
            when (resource) {
                is Resource.Success -> {
                    _state.update {
                        BookmarkScreenState(
                            videoData = resource.data,
                            videoTypeImage = videoTypeImage
                        )
                    }
                }
                is Resource.Loading -> {}
                is Resource.Error -> {
                    _state.update {
                        BookmarkScreenState()
                    }
                    _event.emit(
                        BookmarkEvent.ShowToastMessage(message = resource.errorMsg ?: "오류")
                    )
                }
            }
        }.launchIn(viewModelScope)

    }

    fun setTitle(title: String) {
        if (title.length <= 10) {
            _state.update {
                it.copy(
                    bookmarkTitle = title,
                    isEnable = title.isNotEmpty() && it.videoPosition.isNotEmpty()
                )
            }
        }
    }

    fun setVideoPosition(videoPosition: String) {
        if (videoPosition.length <= 8) {
            _state.update {
                it.copy(
                    videoPosition = videoPosition,
                    isEnable = it.bookmarkTitle.isNotEmpty() && videoPosition.isNotEmpty()
                )
            }
        }
    }

    fun changeBookmarkColor(color: Int) {
        _state.update {
            it.copy(
                selectedColor = color
            )
        }
    }

    // 북마크 추가 및 수정
    fun addOrEditBookmark(editBookmarkId: Int? = null) = viewModelScope.launch {
        // 데이터가 다 들어와 있을 때
        if (_state.value.isEnable) {
            val positionTime = getDateFromPosition(
                _state.value.videoPosition
            )

            // 영상 범위가 정상일 때
            if (checkVideoPosition(positionTime, _state.value.videoData?.duration!!)) {
                val bookmark =
                    Bookmark(
                        id = editBookmarkId,
                        videoId = _state.value.videoData!!.id,
                        title = _state.value.bookmarkTitle,
                        videoPosition = positionTime!!,
                        color = _state.value.selectedColor
                    )
                repository.addBookmark(bookmark)

                if (editBookmarkId == null) { // 북마크 추가
                    val id = repository.getItemId(bookmark)
                    _event.emit(
                        BookmarkEvent.ChangeBookmark(
                            message = "북마크가 추가되었습니다.",
                            bookmark = bookmark.copy(id = id)
                        )
                    )
                } else { // 북마크 수정
                    _event.emit(
                        BookmarkEvent.ChangeBookmark(
                            message = "북마크가 수정되었습니다.",
                            bookmark = bookmark
                        )
                    )
                }
            } else {
                _event.emit(
                    BookmarkEvent.ShowToastMessage(message = "영상 위치가 잘 못 되었습니다.")
                )
            }
        }
    }

    // 값 세팅
    fun initValue(videoData: Video, bookmark: Bookmark, typeImageRes: Int) {
        _state.update {
            BookmarkScreenState(
                videoData = videoData,
                videoTypeImage = typeImageRes,
                bookmarkTitle = bookmark.title,
                videoPosition = getDateStrFromPosition(bookmark.videoPosition),
                selectedColor = bookmark.color,
                isEnable = true
            )
        }
    }

    fun deleteBookmark(bookmark: Bookmark) = viewModelScope.launch {
        repository.deleteBookmark(
            bookmark.copy(title = "", videoPosition = 0)
        )

        _event.emit(
            BookmarkEvent.ChangeBookmark(
                message = "북마크가 삭제 되었습니다.",
                bookmark = bookmark
            )
        )
    }

    fun setDialogState(isOpen: Boolean) {
        _state.update {
            it.copy(
                isOpenDialog = isOpen
            )
        }
    }

    fun getVideoUrl(videoPosition: Long): String {
        val time = getSecondsFromPosition(videoPosition)
        val latestState = _state.value

        return if (latestState.videoTypeImage == R.drawable.youtube) {
            "${latestState.videoData!!.url}&t=$time"
        } else if (latestState.videoTypeImage == R.drawable.twitch) {
            "$${latestState.videoData!!.url}?t=$time"
        } else {
            ""
        }
    }

    private fun getBaseUrl(url: String) =
        url.substringAfter("https://").substringBefore("/")

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

    // url에서 시간을 가져옴
    private fun getTimeFromUrl(url: String): String {
        val time = url.substringAfter("t=")
            .substringBefore("s")
            .toInt() * 1000 // milliSecons와 단위를 맞춤

        return if (time >= 3600000) { // 1시간 이상 일 경우
            SimpleDateFormat("hh:mm:ss", Locale.KOREA)
        } else { // 1시간 이하일 경우
            SimpleDateFormat("mm:ss", Locale.KOREA)
        }.format(time - 32400000)
    }
}