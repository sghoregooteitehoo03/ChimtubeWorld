package com.sghore.chimtubeworld.presentation.bookmarkScreen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.*
import com.sghore.chimtubeworld.R
import com.sghore.chimtubeworld.data.model.Bookmark
import com.sghore.chimtubeworld.data.model.Goods
import com.sghore.chimtubeworld.data.model.Resource
import com.sghore.chimtubeworld.data.model.Video
import com.sghore.chimtubeworld.data.repository.BookmarkRepository
import com.sghore.chimtubeworld.domain.GetTwitchVideoUseCase
import com.sghore.chimtubeworld.domain.GetYoutubeVideoUseCase
import com.sghore.chimtubeworld.presentation.storeDetailScreen.StoreDetailViewModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
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
    private val baseYoutubeUrl = "youtu.be"
    private val baseTwitchUrl = "www.twitch.tv"

    private val _state = MutableStateFlow(BookmarkScreenState(isLoading = true))
    val state = _state.asStateFlow()

    var selectedBookmark: Bookmark? = null
        private set

    init {
        val videoData = savedStateHandle.get<Video>("video") // 영상 데이터
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
                is Resource.Loading -> {
                    _state.update {
                        BookmarkScreenState(
                            isLoading = true
                        )
                    }
                }
                is Resource.Error -> {
                    _state.update {
                        BookmarkScreenState(
                            errorMsg = resource.errorMsg ?: "오류"
                        )
                    }
                }
            }
        }.launchIn(viewModelScope)

    }

    fun setTitle(title: String) {
        if (title.length <= 10) {
            val latestState = _state.value

            latestState.bookmarkInfoState = latestState.bookmarkInfoState.copy(
                bookmarkTitle = title,
                isEnable = title.isNotEmpty() && latestState.bookmarkInfoState.videoPosition.isNotEmpty()
            )
        }
    }

    fun setVideoPosition(videoPosition: String) {
        if (videoPosition.length <= 8) {
            val latestState = _state.value

            latestState.bookmarkInfoState = latestState.bookmarkInfoState.copy(
                videoPosition = videoPosition,
                isEnable = latestState.bookmarkInfoState.bookmarkTitle.isNotEmpty() && videoPosition.isNotEmpty()
            )
        }
    }

    fun changeBookmarkColor(color: Int) {
        val latestState = _state.value

        latestState.bookmarkInfoState = latestState.bookmarkInfoState.copy(
            selectedColor = color
        )
    }

    fun clearMsg() {
        _state.update {
            it.copy(
                errorMsg = ""
            )
        }
    }

    // 북마크 추가 및 수정
    fun addOrEditBookmark(editBookmarkId: Int = -1) = viewModelScope.launch {
        val latestState = _state.value
        val bookmarkInfoState = latestState.bookmarkInfoState
        // 데이터가 다 들어와 있을 때
        if (bookmarkInfoState.isEnable) {
            val positionTime = getDateFromPosition(bookmarkInfoState.videoPosition)

            // 영상 범위가 정상인
            if (checkVideoPosition(positionTime, latestState.videoData?.duration!!)) {
                if (editBookmarkId == -1) { // 북마크 추가
                    val bookmark = Bookmark(
                        videoId = latestState.videoData.id ?: "",
                        title = bookmarkInfoState.bookmarkTitle,
                        videoPosition = positionTime!!,
                        color = bookmarkInfoState.selectedColor
                    )

                    repository.addBookmark(bookmark)

                    val id = repository.getItemId(bookmark)
                    _state.update {
                        it.copy(
                            errorMsg = "북마크가 추가되었습니다.",
                            completeBookmark = bookmark.copy(id = id)
                        )
                    }
                } else { // 북마크 수정
                    val bookmark =
                        Bookmark(
                            id = editBookmarkId,
                            videoId = latestState.videoData.id,
                            title = bookmarkInfoState.bookmarkTitle,
                            videoPosition = positionTime!!,
                            color = bookmarkInfoState.selectedColor
                        )

                    repository.editBookmark(bookmark)
                    _state.update {
                        it.copy(
                            errorMsg = "북마크가 수정되었습니다",
                            completeBookmark = bookmark
                        )
                    }
                }
            } else {
                _state.update {
                    it.copy(
                        errorMsg = "영상 위치가 잘 못 되었습니다."
                    )
                }
            }
        }
    }

    // 값 세팅
    fun initValue(videoData: Video, bookmark: Bookmark, typeImageRes: Int) {
        _state.update {
            BookmarkScreenState(
                videoData = videoData,
                videoTypeImage = typeImageRes
            ).apply {
                this.bookmarkInfoState = BookmarkInfoState(
                    bookmarkTitle = bookmark.title,
                    videoPosition = getDateStrFromPosition(bookmark.videoPosition),
                    selectedColor = bookmark.color,
                    isEnable = true
                )
            }
        }
    }

    fun deleteBookmark(bookmark: Bookmark) = viewModelScope.launch {
        repository.deleteBookmark(bookmark)
        _state.update {
            it.copy(
                errorMsg = "북마크가 삭제 되었습니다.",
                completeBookmark = bookmark.copy(title = "", videoPosition = 0)
            )
        }
    }

    fun setDialogState(isOpen: Boolean) {
        val latestState = _state.value

        _state.update {
            it.copy(
                isOpenDialog = isOpen
            ).apply {
                bookmarkInfoState = latestState.bookmarkInfoState
            }
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

    private fun getBaseUrl(url: String) = url.substringAfter("https://").substringBefore("/")

    // 영상 범위 확인
    private fun checkVideoPosition(positionTime: Long?, duration: Long): Boolean {
        return if (positionTime == null) {
            false
        } else {
            // 0[s] <= position <= duration[s]
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