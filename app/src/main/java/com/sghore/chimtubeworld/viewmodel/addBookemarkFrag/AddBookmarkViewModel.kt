package com.sghore.chimtubeworld.viewmodel.addBookemarkFrag

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sghore.chimtubeworld.R
import com.sghore.chimtubeworld.data.Bookmark
import com.sghore.chimtubeworld.data.Video
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class AddBookmarkViewModel @Inject constructor(
    private val repository: AddBookmarkRepository
) : ViewModel() {
    private val baseYoutubeUrl = "youtu.be"
    private val baseTwitchUrl = "www.twitch.tv"

    private val _isEnable = MutableLiveData(false) // 버튼 enable
    private val _isLoading = MutableLiveData(true) // 로딩 여부
    private val _isError = MutableLiveData(false) // 오류 여부
    private val _isComplete = MutableLiveData(false) // 완료 여부
    private val _videoData = MutableLiveData<Video?>(null) // 영상 데이터
    private val _videoTypeImage = MutableLiveData(-1) // 영상 타입 이미지

    val isEnable: LiveData<Boolean> = _isEnable
    val isLoading: LiveData<Boolean> = _isLoading
    val isError: LiveData<Boolean> = _isError
    val isComplete: LiveData<Boolean> = _isComplete
    val videoData: LiveData<Video?> = _videoData
    val videoTypeImage: LiveData<Int> = _videoTypeImage

    val bookmarkTitle = MutableLiveData("") // 북마크 제목
    val videoPosition = MutableLiveData("") // 영상 위치
    val bookmarkColor = MutableLiveData(-1) // 북마크 색
    val selectedPos = MutableLiveData(0) // 북마크 색 선택 위치
    val toastMsg = MutableLiveData("") // 토스트 메세지

    // 넘겨온 영상 url을 통해 영상 정보를 가져옴
    fun getVideoData(videoUrl: String) = viewModelScope.launch {
        try {
            val baseUrl = getBaseUrl(videoUrl)

            _videoData.value = if (baseUrl == baseYoutubeUrl) { // 유튜브에서 넘어온 링크일 경우
                _videoTypeImage.value = R.drawable.ic_youtube
                repository.getVideoFromYoutube(url = videoUrl, baseUrl = baseUrl)
            } else if (baseUrl == baseTwitchUrl) { // 트위치에서 넘어온 링크일 경우
                _videoTypeImage.value = R.drawable.ic_twitch
                repository.getVideoFromTwitch(url = videoUrl, baseUrl = baseUrl)
            } else {
                null
            }

            if (_videoData.value == null) {
                throw NullPointerException()
            }

            _isLoading.value = false // 로딩 끝
        } catch (e: Exception) {
            toastMsg.value = "지원하지 않는 형식의 URL입니다."
            _isError.value = true
            e.printStackTrace()
        }
    }

    // 북마크 추가 및 수정
    fun addOrEditBookmark(editBookmarkId: Int = -1) = viewModelScope.launch {
        val videoId = videoData.value!!.id
        val title = bookmarkTitle.value!!
        val position = videoPosition.value!!
        val color = bookmarkColor.value!!
        val isEnable = _isEnable.value!!

        // 데이터가 다 들어와 있을 때
        if (isEnable) {
            val positionTime = getTimeToLongData(position)

            // 영상 범위가 정상인지
            if (checkVideoPosition(positionTime)) {
                if (editBookmarkId == -1) { // 북마크 추가
                    val bookmark =
                        Bookmark(
                            videoId = videoId,
                            title = title,
                            videoPosition = positionTime!!,
                            color = color
                        )

                    repository.addBookmark(bookmark)
                    toastMsg.value = "북마크가 추가되었습니다"
                } else { // 북마크 수정
                    val bookmark =
                        Bookmark(
                            id = editBookmarkId,
                            videoId = videoId,
                            title = title,
                            videoPosition = positionTime!!,
                            color = color
                        )

                    repository.editBookmark(bookmark)
                    toastMsg.value = "북마크가 수정되었습니다"
                }

                _isComplete.value = true // 작업 완료
            } else {
                toastMsg.value = "영상 위치가 잘 못 되었습니다."
            }
        }
    }

    // 데이터 유효성 검사
    fun checkData() {
        val title = bookmarkTitle.value!!
        val position = videoPosition.value!!
        val color = bookmarkColor.value!!

        _isEnable.value = title.isNotEmpty() && position.isNotEmpty() && color != -1
    }

    // 값 세팅
    fun initValue(videoData: Video, bookmark: Bookmark, typeImageRes: Int, colorIndex: Int) {
        _videoData.value = videoData
        _videoTypeImage.value = typeImageRes

        bookmarkTitle.value = bookmark.title
        videoPosition.value = getTimeToStringData(bookmark.videoPosition)
        bookmarkColor.value = bookmark.color
        selectedPos.value = colorIndex

        _isLoading.value = false
    }

    fun deleteBookmark(bookmark: Bookmark) = viewModelScope.launch {
        repository.deleteBookmark(bookmark)

        toastMsg.value = "북마크가 삭제 되었습니다."
        _isComplete.value = true // 작업 완료
    }

    fun getVideoUrl(videoPosition: Long): String {
        val time = getTimeToStringDataForClipBoard(videoPosition)

        return if (_videoTypeImage.value == R.drawable.ic_youtube) {
            "${_videoData.value!!.url}&t=$time"
        } else if (_videoTypeImage.value == R.drawable.ic_twitch) {
            "${_videoData.value!!.url}?t=$time"
        } else {
            ""
        }
    }

    private fun getBaseUrl(url: String) = url.substringAfter("https://").substringBefore("/")

    // 영상 범위 확인
    private fun checkVideoPosition(positionTime: Long?): Boolean {
        return if (positionTime == null) {
            false
        } else {
            // 0[s] <= position <= duration[s]
            -32400000 <= positionTime && positionTime <= videoData.value?.duration!!
        }
    }

    // 입력한 시간을 시:분:초 데이터로 파싱하여 Long 데이터로 반환
    private fun getTimeToLongData(videoPosition: String): Long? {
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
    private fun getTimeToStringData(videoPosition: Long): String {
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

    // 시간 데이터를 시h:분m:초s String으로 변환하여 반환
    private fun getTimeToStringDataForClipBoard(videoPosition: Long): String {
        val dateFormat = if (videoPosition >= -28800000) { // 01:00:00
            SimpleDateFormat(
                "hh\'h\'mm\'m\'ss\'s\'",
                Locale.KOREA
            )
        } else {
            SimpleDateFormat(
                "mm\'m\'ss\'s\'",
                Locale.KOREA
            )
        }

        return dateFormat.format(videoPosition)
    }
}