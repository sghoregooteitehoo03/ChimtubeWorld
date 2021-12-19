package com.sghore.chimtubeworld.adapter.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.google.firebase.firestore.FirebaseFirestore
import com.sghore.chimtubeworld.data.Video
import com.sghore.chimtubeworld.other.Contents
import com.sghore.chimtubeworld.retrofit.RetrofitService
import kotlinx.coroutines.tasks.await
import retrofit2.Retrofit
import retrofit2.await
import java.text.SimpleDateFormat
import java.util.*
import kotlin.time.Duration

class YoutubePagingSource(
    private val channelId: String,
    private val retrofitBuilder: Retrofit.Builder
) : PagingSource<String, Video>() {
    override fun getRefreshKey(state: PagingState<String, Video>): String? {
        return null
    }

    override suspend fun load(params: LoadParams<String>): LoadResult<String, Video> {
        return try {
            val pageKey = params.key // 페이지 키
            val retrofitService = retrofitBuilder.baseUrl(Contents.YOUTUBE_API_URL)
                .build()
                .create(RetrofitService::class.java)

            val playlistItems = retrofitService.getYPlaylistItems(
                channelId,
                pageKey
            ).await() // 채널의 최근 동영상 리스트의 아이디를 가져옴
            val videoIdArray = playlistItems.items.map { it.contentDetails.videoId }
                .toTypedArray() // 동영상 아이디 배열

            val nextKey = playlistItems.nextPageToken // 다음 페이지
            val videoList = getYoutubeVideos(retrofitService, videoIdArray) // 동영상 리스트

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

    // 유튜브 영상 정보 가져오기
    private suspend fun getYoutubeVideos(
        retrofitService: RetrofitService,
        videoIdArray: Array<String>
    ): List<Video> {
        val videosResponse = retrofitService.getYVideos(videoIdArray)
            .await()

        return videosResponse.items.map { response ->
            val dateFormat = SimpleDateFormat(
                "yyyy-MM-dd'T'HH:mm:ss'Z'",
                Locale.KOREA
            )
            // 업로드 시간
            val uploadTime = dateFormat.parse(response.snippet.publishedAt)
                ?.time ?: 0 + 32400000 // (+9 Hours) UTC -> KOREA
            // 영상 길이
            val duration = Duration.parse(response.contentDetails.duration)
                .inWholeMilliseconds - 32400000 // (-9 Hours) KOREA -> UTC

            Video(
                id = response.id,
                title = response.snippet.title, // 제목
                thumbnail = response.snippet.thumbnails.medium.url, // 썸네일 이미지
                uploadTime = uploadTime,
                viewCount = response.statistics.viewCount.toLong(), // 조회수
                duration = duration,
                url = "https://www.youtube.com/watch?v=${response.id}"
            )
        }
    }
}