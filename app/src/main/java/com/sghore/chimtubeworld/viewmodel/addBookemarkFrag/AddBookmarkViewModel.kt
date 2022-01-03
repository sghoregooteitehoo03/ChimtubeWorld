package com.sghore.chimtubeworld.viewmodel.addBookemarkFrag

import android.util.Log
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
    val errorMsg = MutableLiveData("") // 에러 메세지

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
            _isError.value = true
            errorMsg.value = "지원하지 않는 형식의 URL입니다."
            e.printStackTrace()
        }
    }

    // 북마크 추가
    fun addBookmark() = viewModelScope.launch {
        val title = bookmarkTitle.value!!
        val position = videoPosition.value!!
        val color = bookmarkColor.value!!

        // 데이터가 다 들어와 있을 때
        if (title.isNotEmpty() && position.isNotEmpty() && color != -1) {
            val positionTime = getTimeToLongData(position)

            if (checkVideoPosition(positionTime)) {
                val bookmark =
                    Bookmark(title = title, videoPosition = positionTime!!, color = color)

                repository.addBookmark(bookmark)
                _isComplete.value = true // 작업 완료
            } else {
                errorMsg.value = "영상 위치가 잘 못 되었습니다."
            }
        }
    }

    private fun getBaseUrl(url: String) = url.substringAfter("https://").substringBefore("/")

    fun checkData() {
        val title = bookmarkTitle.value!!
        val position = videoPosition.value!!
        val color = bookmarkColor.value!!

        _isEnable.value = title.isNotEmpty() && position.isNotEmpty() && color != -1
    }

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
}