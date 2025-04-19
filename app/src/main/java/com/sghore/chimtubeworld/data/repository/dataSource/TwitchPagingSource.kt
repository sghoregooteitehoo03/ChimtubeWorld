package com.sghore.chimtubeworld.data.repository.dataSource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.google.firebase.firestore.FirebaseFirestore
import com.sghore.chimtubeworld.data.model.Video
import com.sghore.chimtubeworld.data.db.Dao
import com.sghore.chimtubeworld.other.Constants
import com.sghore.chimtubeworld.data.retrofit.RetrofitService
import com.sghore.chimtubeworld.data.retrofit.dto.twitchAPI.VideosDataDTO
import kotlinx.coroutines.tasks.await
import retrofit2.await
import java.text.SimpleDateFormat
import java.util.*
import kotlin.time.Duration

class TwitchPagingSource(
    private val channelId: String,
    private val retrofitService: RetrofitService,
    private val store: FirebaseFirestore,
    private val dao: Dao
) : PagingSource<String, Video>() {
    override fun getRefreshKey(state: PagingState<String, Video>): String? {
        return null
    }

    override suspend fun load(params: LoadParams<String>): PagingSource.LoadResult<String, Video> {
        return try {
            val pageKey = params.key ?: "" // 페이지 키

            // 동영상 리스트
            val document = store.collection(Constants.COLLECTION_TWITCH_LINK)
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
            val videoList = translateVideoData(videosResult.data, pageKey) // 영상 리스트

            LoadResult.Page(
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
    private suspend fun translateVideoData(videosData: List<VideosDataDTO>, currentPage: String) =
        videosData.map { videoData ->
            val bookmarks = dao.getBookmarks(videoData.id) // 해당 영상 아이디에 해당하는 북마크를 가져옴
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
                url = videoData.url,
                bookmarks = bookmarks
            )
        }
}