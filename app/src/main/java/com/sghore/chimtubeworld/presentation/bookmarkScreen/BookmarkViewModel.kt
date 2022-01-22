package com.sghore.chimtubeworld.presentation.bookmarkScreen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.*
import com.sghore.chimtubeworld.R
import com.sghore.chimtubeworld.data.model.Bookmark
import com.sghore.chimtubeworld.data.model.Resource
import com.sghore.chimtubeworld.data.model.Video
import com.sghore.chimtubeworld.data.repository.BookmarkRepository
import com.sghore.chimtubeworld.domain.GetTwitchVideoUseCase
import com.sghore.chimtubeworld.domain.GetYoutubeVideoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class BookmarkViewModel @Inject constructor(
    private val getYoutubeVideoUseCase: GetYoutubeVideoUseCase,
    private val getTwitchVideoUseCase: GetTwitchVideoUseCase,
    private val repository: BookmarkRepository
) : ViewModel() {
    private val baseYoutubeUrl = "youtu.be"
    private val baseTwitchUrl = "www.twitch.tv"

    var state by mutableStateOf(BookmarkScreenState())
        private set

    // 넘겨온 영상 url을 통해 영상 정보를 가져옴
    fun getVideoData(videoUrl: String) {
        state.videoData ?: let {
            val baseUrl = getBaseUrl(videoUrl)
            var videoTypeImage = -1

            val videoFlow = if (baseUrl == baseYoutubeUrl) {
                videoTypeImage = R.drawable.ic_youtube
                getYoutubeVideoUseCase(videoUrl, baseUrl)
            } else {
                videoTypeImage = R.drawable.ic_twitch
                getTwitchVideoUseCase(videoUrl, baseUrl)
            }

            videoFlow.onEach { resource ->
                when (resource) {
                    is Resource.Success -> {
                        state = BookmarkScreenState(
                            videoData = resource.data,
                            videoTypeImage = videoTypeImage
                        )
                    }
                    is Resource.Loading -> {
                        state = BookmarkScreenState(
                            isLoading = true
                        )
                    }
                    is Resource.Error -> {
                        state = BookmarkScreenState(
                            errorMsg = resource.errorMsg ?: "오류"
                        )
                    }
                }
            }.launchIn(viewModelScope)
        }
    }

    fun setTitle(title: String) {
        if (title.length <= 10) {
            state = state.copy(
                bookmarkTitle = title,
                isEnable = title.isNotEmpty() && state.videoPosition.isNotEmpty()
            )
        }
    }

    fun setVideoPosition(videoPosition: String) {
        if (videoPosition.length <= 8) {
            state = state.copy(
                videoPosition = videoPosition,
                isEnable = state.bookmarkTitle.isNotEmpty() && videoPosition.isNotEmpty()
            )
        }
    }

    fun changeBookmarkColor(color: Int) {
        state = state.copy(
            selectedColor = color
        )
    }

    fun clearMsg() {
        state = state.copy(
            errorMsg = ""
        )
    }

    // 북마크 추가 및 수정
    fun addOrEditBookmark(editBookmarkId: Int = -1) = viewModelScope.launch {
        // 데이터가 다 들어와 있을 때
        if (state.isEnable) {
            val positionTime = getDateFromPosition(state.videoPosition)

            // 영상 범위가 정상인
            if (checkVideoPosition(positionTime, state.videoData?.duration!!)) {
                if (editBookmarkId == -1) { // 북마크 추가
                    val bookmark = Bookmark(
                        videoId = state.videoData?.id ?: "",
                        title = state.bookmarkTitle,
                        videoPosition = positionTime!!,
                        color = state.selectedColor
                    )

                    repository.addBookmark(bookmark)
                    state = state.copy(
                        errorMsg = "북마크가 추가되었습니다.",
                        isComplete = true
                    )
                } else { // 북마크 수정
                    val bookmark =
                        Bookmark(
                            id = editBookmarkId,
                            videoId = state.videoData?.id ?: "",
                            title = state.bookmarkTitle,
                            videoPosition = positionTime!!,
                            color = state.selectedColor
                        )

                    repository.editBookmark(bookmark)
                    state = state.copy(
                        errorMsg = "북마크가 수정되었습니다",
                        isComplete = true
                    )
                }
            } else {
                state = state.copy(
                    errorMsg = "영상 위치가 잘 못 되었습니다."
                )
            }
        }
    }

    // 값 세팅
    fun initValue(videoData: Video, bookmark: Bookmark, typeImageRes: Int) {
        state.videoData ?: let {
            state = BookmarkScreenState(
                bookmarkTitle = bookmark.title,
                videoPosition = getDateStrFromPosition(bookmark.videoPosition),
                videoData = videoData,
                videoTypeImage = typeImageRes,
                selectedColor = bookmark.color,
                isEnable = true
            )
        }
    }

    fun deleteBookmark(bookmark: Bookmark) = viewModelScope.launch {
        repository.deleteBookmark(bookmark)

        state = BookmarkScreenState(
            errorMsg = "북마크가 삭제 되었습니다.",
            isComplete = true
        )
    }

    fun getVideoUrl(videoPosition: Long): String {
        val time = getSecondsFromPosition(videoPosition)

        return if (state.videoTypeImage == R.drawable.ic_youtube) {
            "${state.videoData!!.url}&t=$time"
        } else if (state.videoTypeImage == R.drawable.ic_twitch) {
            "$${state.videoData!!.url}?t=$time"
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