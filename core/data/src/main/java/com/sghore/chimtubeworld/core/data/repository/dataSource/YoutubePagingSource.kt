package com.sghore.chimtubeworld.core.data.repository.dataSource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.sghore.chimtubeworld.core.network.datasource.YoutubeRemoteDataSource
import com.sghore.chimtubeworld.core.model.Video
import com.sghore.chimtubeworld.core.database.dao.Dao
import com.sghore.chimtubeworld.core.data.mapper.toDomain
import java.text.SimpleDateFormat
import java.util.*
import kotlin.time.Duration

internal class YoutubePagingSource(
    private val channelId: String,
    private val remoteDataSource: YoutubeRemoteDataSource,
    private val dao: Dao
) : PagingSource<String, Video>() {
    override fun getRefreshKey(state: PagingState<String, Video>): String? {
        return null
    }

    override suspend fun load(params: LoadParams<String>): LoadResult<String, Video> {
        return try {
            val pageKey = params.key // 페이지 키

            val playlistItems = remoteDataSource.getYPlaylistItems(
                channelId,
                pageKey
            ) // 채널의 최근 동영상 리스트의 아이디를 가져옴
            val videoIdArray = playlistItems.items.map { it.contentDetails.videoId }
                .toTypedArray() // 동영상 아이디 배열

            val nextKey = playlistItems.nextPageToken // 다음 페이지
            val videoList = getYoutubeVideos(
                remoteDataSource = remoteDataSource,
                videoIdArray = videoIdArray,
                currentPageKey = pageKey
            ) // 동영상 리스트

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

    // 유튜브 영상 정보 가져오기
    private suspend fun getYoutubeVideos(
        remoteDataSource: YoutubeRemoteDataSource,
        videoIdArray: Array<String>,
        currentPageKey: String?
    ): List<Video> {
        val videosResponse = remoteDataSource.getYVideos(videoIdArray)

        return videosResponse.items.map { response ->
            val bookmarks = dao.getBookmarks(response.id).map { it.toDomain() } // 해당 영상 아이디에 해당하는 북마크를 가져옴
            val dateFormat = SimpleDateFormat(
                "yyyy-MM-dd'T'HH:mm:ss'Z'",
                Locale.KOREA
            )
            // 업로드 시간
            val uploadTime =
                dateFormat.parse(response.snippet.publishedAt)!!.time + 32400000 // (+9 Hours) UTC -> KOREA
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
                url = "https://www.youtube.com/watch?v=${response.id}",
                bookmarks = bookmarks
            )
        }
    }
}
