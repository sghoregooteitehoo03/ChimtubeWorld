package com.sghore.chimtubeworld.data.repository

import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.google.firebase.firestore.FirebaseFirestore
import com.sghore.chimtubeworld.data.db.Dao
import com.sghore.chimtubeworld.data.model.Channel
import com.sghore.chimtubeworld.data.model.LinkInfo
import com.sghore.chimtubeworld.data.model.Video
import com.sghore.chimtubeworld.data.repository.dataSource.TwitchPagingSource
import com.sghore.chimtubeworld.other.Contents
import com.sghore.chimtubeworld.data.retrofit.RetrofitService
import com.sghore.chimtubeworld.data.retrofit.dto.twitchAPI.UserDataDTO
import kotlinx.coroutines.tasks.await
import retrofit2.Retrofit
import retrofit2.await
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.time.Duration

class TwitchRepository @Inject constructor(
    private val store: FirebaseFirestore,
    private val retrofitBuilder: Retrofit.Builder,
    private val dao: Dao
) {

    // 트위치 영상을 페이징하여 가져옴
    fun getVideos(channelId: String) =
        Pager(PagingConfig(20)) {
            val retrofitService = getRetrofit()
            TwitchPagingSource(
                channelId = channelId,
                retrofitService = retrofitService,
                dao = dao,
                store = store
            )
        }.flow

    // Twitch Id 및 설명을 가져옴
    suspend fun getChannelLinkData() =
        store.collection(Contents.COLLECTION_TWITCH_LINK)
            .whereNotEqualTo("explain", "")
            .get()
            .await()
            .documents
            .map { document ->
                LinkInfo(
                    id = document["id"] as String,
                    url = document["url"]?.toString() ?: "",
                    explain = document["explain"]?.toString() ?: "",
                    type = (document["type"] as Long).toInt(),
                    playlistId = document["playlistId"] as String,
                    playlistName = document["playlistName"] as String
                )
            }

    // Api 키를 가져옴
    suspend fun getTwitchAccessKey() =
        "Bearer " + store.collection(Contents.COLLECTION_TWITCH_LINK) // API 요청 키
            .document("-1")
            .get()
            .await()["AUTH"]
            .toString()

    // 트위치 채널의 정보를 가져옴
    suspend fun getTwitchUserInfo(
        channelLinkList: List<LinkInfo>,
        accessKey: String
    ): List<Channel> {
        val retrofitService = getRetrofit()

        // Twitch API를 통해 채널의 정보를 가져옴
        val result = retrofitService.getTUserInfo(
            accessKey,
            channelLinkList.map { it.id }.toTypedArray()
        )
        // 채널 아이디 리스트
        val channelIdList = result.data.map { it.login }

        return channelLinkList.map { linkInfo ->
            // API에서 넘어온 데이터가 linkInfo와 일치하는 데이터의 Index값
            val index = channelIdList.indexOf(linkInfo.id)
            val userData = result.data[index]

            if (linkInfo.type == 0) { // 침착맨 채널의 정보일 때
                getTwitchUserState(
                    retrofitService,
                    accessKey,
                    userData,
                    linkInfo
                )
            } else {
                Channel(
                    id = userData.id,
                    playlistId = userData.id,
                    playlistName = linkInfo.playlistName,
                    name = userData.displayName,
                    explains = arrayOf(linkInfo.explain),
                    url = linkInfo.url,
                    image = userData.profile_image_url,
                    thumbnailImage = userData.offline_image_url,
                    type = linkInfo.type,
                    isOnline = false
                )
            }
        }
    }

    // 트위치에서 영상 정보를 가져옴
    suspend fun getVideo(
        url: String,
        baseUrl: String,
        accessKey: String
    ): Video {
        val videoId = url.substringAfter("https://${baseUrl}/")
            .substringAfter("/v/", "")
            .substringBefore("?sr")

        if (videoId.isEmpty()) { // 오류 발생 시
            throw NullPointerException()
        }

        val retrofit = getRetrofit()
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

    // 침착맨의 팔로워 방송여부 등을 가져옴
    private suspend fun getTwitchUserState(
        retrofitService: RetrofitService,
        accessKey: String,
        userInfo: UserDataDTO,
        linkInfo: LinkInfo
    ): Channel {
        val followData = retrofitService.getTUserFollows(accessKey, userInfo.id) // 팔로우 수
        val streamData = retrofitService.getTUserStream(accessKey, userInfo.login) // 방송 데이터
        val isOnline: Boolean // 방송 여부

        // 썸네일
        val thumbnailImage = if (streamData.data.isEmpty()) {
            // 방송이 오프라인 일 때
            isOnline = false
            userInfo.offline_image_url
        } else {
            // 방송이 온라인 일 때
            isOnline = true
            streamData.data[0].thumbnailUrl
                .replace("{width}", "1920")
                .replace("{height}", "1080")
        }

        val channelData = Channel(
            id = userInfo.id,
            playlistId = userInfo.id,
            playlistName = linkInfo.playlistName,
            name = userInfo.displayName,
            explains = arrayOf(linkInfo.explain, followData.total.toString()),
            url = linkInfo.url,
            image = userInfo.profile_image_url,
            thumbnailImage = thumbnailImage,
            type = linkInfo.type,
            isOnline = isOnline
        )

        Log.i("Check", "data: $channelData")
        return channelData
    }

    private fun getRetrofit() =
        retrofitBuilder.baseUrl(Contents.TWITCH_API_URL)
            .build()
            .create(RetrofitService::class.java)
}