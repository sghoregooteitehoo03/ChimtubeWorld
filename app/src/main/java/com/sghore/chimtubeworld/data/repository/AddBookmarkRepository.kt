package com.sghore.chimtubeworld.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.sghore.chimtubeworld.data.model.Bookmark
import com.sghore.chimtubeworld.data.model.Video
import com.sghore.chimtubeworld.data.db.Dao
import com.sghore.chimtubeworld.other.Contents
import com.sghore.chimtubeworld.data.retrofit.RetrofitService
import kotlinx.coroutines.tasks.await
import retrofit2.Retrofit
import retrofit2.await
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.time.Duration

class AddBookmarkRepository @Inject constructor(
    private val store: FirebaseFirestore,
    private val retrofitBuilder: Retrofit.Builder,
    private val dao: Dao
) {

    // 유튜브에서 영상 정보를 가져옴
    suspend fun getVideoFromYoutube(url: String, baseUrl: String): Video {
        val videoId = url.substringAfter("https://${baseUrl}/")
        if (videoId.isEmpty()) { // 오류 발생 시
            throw NullPointerException()
        }

        val retrofit = getRetrofit(Contents.YOUTUBE_API_URL)
        val videoData = retrofit.getYVideos(arrayOf(videoId))
            .await()

        return videoData.items[0].let { response ->
            val dateFormat = SimpleDateFormat(
                "yyyy-MM-dd'T'HH:mm:ss'Z'",
                Locale.KOREA
            )
            // 업로드 시간
            val uploadTime =
                dateFormat.parse(response.snippet.publishedAt).time + 32400000 // (+9 Hours) UTC -> KOREA
            // 영상 길이
            val duration = Duration.parse(response.contentDetails.duration)
                .inWholeMilliseconds - 32400000 // (-9 Hours) KOREA -> UTC

            Video(
                id = response.id,
                channelName = response.snippet.channelTitle,
                title = response.snippet.title, // 제목
                thumbnail = response.snippet.thumbnails.high.url, // 썸네일 이미지
                uploadTime = uploadTime,
                viewCount = response.statistics.viewCount.toLong(), // 조회수
                duration = duration,
                url = "https://www.youtube.com/watch?v=${response.id}"
            )
        }
    }

    // 트위치에서 영상 정보를 가져옴
    suspend fun getVideoFromTwitch(url: String, baseUrl: String): Video {
        val videoId = url.substringAfter("https://${baseUrl}/")
            .substringAfter("/v/", "")
            .substringBefore("?sr")

        if (videoId.isEmpty()) { // 오류 발생 시
            throw NullPointerException()
        }

        val retrofit = getRetrofit(Contents.TWITCH_API_URL)
        val accessKey = "Bearer " + store.collection(Contents.COLLECTION_TWITCH_LINK) // API 요청 키
            .document("-1")
            .get()
            .await()["AUTH"]
            .toString()
        val videoData = retrofit.getTVideoFromVideoId(
            accessKey = accessKey,
            videoId = videoId
        ).await()

        return videoData.data[0].let { response ->
            val dateFormat = SimpleDateFormat(
                "yyyy-MM-dd'T'HH:mm:ss'Z'",
                Locale.KOREA
            )
            // 업로드 시간
            val uploadTime =
                dateFormat.parse(response.publishedAt).time + 32400000 // (+9 Hours) UTC -> KOREA
            // 영상 길이
            val duration = Duration.parse(response.duration)
                .inWholeMilliseconds - 32400000 // (-9 Hours) KOREA -> UTC
            val thumbnailImage = response.thumbnailUrl // 썸네일 이미지
                .replace("%{width}", "1920")
                .replace("%{height}", "1080")

            Video(
                id = response.id, // 아이디
                channelName = response.userName,
                title = response.title, // 제목
                thumbnail = thumbnailImage,
                uploadTime = uploadTime,
                viewCount = response.viewCount, // 조회수
                duration = duration,
                url = response.url
            )
        }
    }

    // 북마크 추가
    suspend fun addBookmark(bookmark: Bookmark) {
        dao.insertBookmark(bookmark)
    }

    // 북마크 수정
    suspend fun editBookmark(bookmark: Bookmark) {
        dao.updateBookmark(bookmark)
    }

    // 북마크 삭제
    suspend fun deleteBookmark(bookmark: Bookmark) {
        dao.deleteBookmark(bookmark)
    }

    private fun getRetrofit(baseUrl: String) =
        retrofitBuilder.baseUrl(baseUrl)
            .build()
            .create(RetrofitService::class.java)
}