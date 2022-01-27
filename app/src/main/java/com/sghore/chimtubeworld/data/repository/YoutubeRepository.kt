package com.sghore.chimtubeworld.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.google.firebase.firestore.FirebaseFirestore
import com.sghore.chimtubeworld.data.db.Dao
import com.sghore.chimtubeworld.data.model.Channel
import com.sghore.chimtubeworld.data.model.LinkInfo
import com.sghore.chimtubeworld.data.model.Video
import com.sghore.chimtubeworld.data.repository.dataSource.YoutubePagingSource
import com.sghore.chimtubeworld.other.Contents
import com.sghore.chimtubeworld.data.retrofit.RetrofitService
import kotlinx.coroutines.tasks.await
import retrofit2.Retrofit
import retrofit2.await
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.time.Duration

class YoutubeRepository @Inject constructor(
    private val store: FirebaseFirestore,
    private val retrofitBuilder: Retrofit.Builder,
    private val dao: Dao
) {

    // 유튜브 영상을 페이징하여 가져옴
    fun getVideos(channelId: String) =
        Pager(PagingConfig(20)) {
            val retrofitService = getRetrofit()
            YoutubePagingSource(
                channelId = channelId,
                retrofitService = retrofitService,
                dao = dao
            )
        }.flow

    // 채널의 Id 및 API에서 가져오지 못하는 부가설명을 가져옴
    suspend fun getChannelLinkData() =
        store.collection(Contents.COLLECTION_YOUTUBE_LINK)
            .get()
            .await()
            .map { document ->
                LinkInfo(
                    id = document["id"] as String,
                    url = document["url"] as String,
                    explain = document["explain"] as String,
                    type = (document["type"] as Long).toInt()
                )
            }

    // 채널의 정보를 가져옴
    suspend fun getChannelInfo(channelLinkList: List<LinkInfo>): List<Channel?> {
        val retrofitService = getRetrofit()

        // 채널 리스트
        val channelList = arrayOfNulls<Channel>(channelLinkList.size)
            .toMutableList()
        // 채널 아이디 배열            [0] = 채널아이디, [1] = 플레이리스트 아이디
        val channelIdArr = channelLinkList.map { it.id.split("|")[0] }
            .toTypedArray()

        // API를 통해 채널들의 정보를 가져옴
        val result = retrofitService.getYChannelInfo(channelIdArr)

        result.items.forEach { channelInfo ->
            // 채널들을 배열순서에 맞쳐 리스트에 집어넣기 위한 인덱스 값
            val index = channelIdArr.indexOf(channelInfo.id)
            val linkInfo = channelLinkList[index]
            val explain = if (linkInfo.type == 0) {
                channelInfo.snippet.description
            } else {
                linkInfo.explain
            }

            val channelData = Channel(
                id = linkInfo.id,
                name = channelInfo.snippet.title,
                explains = arrayOf(explain),
                url = linkInfo.url,
                image = channelInfo.snippet.thumbnails.medium.url,
                type = linkInfo.type
            )

            channelList[index] = channelData
        }

        return channelList.toList()
    }

    // 유튜브에서 영상 정보를 가져옴
    suspend fun getVideo(url: String, baseUrl: String): Video {
        val videoId = url.substringAfter("https://${baseUrl}/")
        if (videoId.isEmpty()) { // 오류 발생 시
            throw NullPointerException()
        }

        val retrofit = getRetrofit()
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

    private fun getRetrofit() =
        retrofitBuilder.baseUrl(Contents.YOUTUBE_API_URL)
            .build()
            .create(RetrofitService::class.java)
}