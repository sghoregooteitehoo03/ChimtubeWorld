package com.sghore.chimtubeworld.adapter.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.google.firebase.firestore.FirebaseFirestore
import com.sghore.chimtubeworld.data.Video
import com.sghore.chimtubeworld.other.Contents
import com.sghore.chimtubeworld.retrofit.RetrofitService
import com.sghore.chimtubeworld.retrofit.dto.twitchAPI.VideosDataDTO
import kotlinx.coroutines.tasks.await
import retrofit2.Retrofit
import retrofit2.await
import java.text.SimpleDateFormat
import java.util.*
import kotlin.time.Duration

class TwitchPagingSource(
    private val channelId: String,
    private val retrofitBuilder: Retrofit.Builder,
    private val store: FirebaseFirestore
) : PagingSource<String, Video>() {
    override fun getRefreshKey(state: PagingState<String, Video>): String? {
        return null
    }

    override suspend fun load(params: LoadParams<String>): PagingSource.LoadResult<String, Video> {
        return try {
            val pageKey = params.key // 페이지 키
            val retrofitService = retrofitBuilder.baseUrl(Contents.TWITCH_API_URL)
                .build()
                .create(RetrofitService::class.java)

            // 동영상 리스트
            val document = store.collection(Contents.COLLECTION_TWITCH_LINK)
                .document("-1")
                .get()
                .await()
            val accessKey = document.get("AUTH") // TWITCH API AUTH KEY
                .toString()
            // 해당 채널의 동영상들을 읽어옴
            val videosResult = retrofitService.getTVideosFromUserId(
                "Bearer $accessKey",
                channelId,
                pageKey
            ).await()

            val nextKey = videosResult.pagination.cursor // 다음 페이지
            val videoList = translateVideoData(videosResult.data) // 영상 리스트

            return LoadResult.Page(
                data = videoList,
                prevKey = null,
                nextKey = nextKey
            )
        } catch (e: Exception) {
            e.printStackTrace()
            LoadResult.Error(e)
        }
    }

    // Video 데이터 포맷에 맞게 변환
    private fun translateVideoData(videosData: List<VideosDataDTO>) =
        videosData.map { videoData ->
            val dateFormat = SimpleDateFormat(
                "yyyy-MM-dd'T'HH:mm:ss'Z'",
                Locale.KOREA
            )
            // 업로드 시간
            val uploadTime =
                dateFormat.parse(videoData.publishedAt).time + 32400000 // (+9 Hours) UTC -> KOREA
            // 영상 길이
            val duration = Duration.parse(videoData.duration)
                .inWholeMilliseconds - 32400000 // (-9 Hours) KOREA -> UTC
            val thumbnailImage = videoData.thumbnailUrl // 썸네일 이미지
                .replace("%{width}", "1920")
                .replace("%{height}", "1080")

            Video(
                id = videoData.id, // 아이디
                channelName = videoData.userName,
                title = videoData.title, // 제목
                thumbnail = thumbnailImage,
                uploadTime = uploadTime,
                viewCount = videoData.viewCount, // 조회수
                duration = duration,
                url = videoData.url
            )
        }
}