package com.sghore.chimtubeworld.data.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.sghore.chimtubeworld.data.model.Channel
import com.sghore.chimtubeworld.data.model.LinkInfo
import com.sghore.chimtubeworld.other.Contents
import com.sghore.chimtubeworld.data.retrofit.RetrofitService
import com.sghore.chimtubeworld.data.retrofit.dto.twitchAPI.UserDataDTO
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.tasks.await
import retrofit2.Retrofit
import retrofit2.await
import javax.inject.Inject

class TwitchRepository @Inject constructor(
    private val store: FirebaseFirestore,
    private val retrofitBuilder: Retrofit.Builder
) {

    // 트위치 채널의 정보를 가져옴
    suspend fun getTwitchUserInfo(): List<Channel?> {
        val retrofitService = retrofitBuilder.baseUrl(Contents.TWITCH_API_URL)
            .build()
            .create(RetrofitService::class.java)
        // Twitch Id 및 설명을 가져옴
        val documents = store.collection(Contents.COLLECTION_TWITCH_LINK)
            .get()
            .await()
            .documents
        val accessKey = "Bearer ${documents[0].get("AUTH").toString()}" // API 액세스 키

        // 채널 리스트
        val channelList = arrayOfNulls<Channel>(documents.size - 1) // AccessKey 제외
            .toMutableList()
        // 채널 아이디 배열
        val channelIdArr = documents.filter { (it["type"] as Long).toInt() != -1 }.map {
            (it["id"] as String)
        }.toTypedArray()

        // Twitch API를 통해 채널의 정보를 가져옴
        val result = retrofitService.getTUserInfo(accessKey, channelIdArr)

        result.data.forEach { userData ->
            // 채널들을 배열순서에 맞쳐 리스트에 집어넣기 위한 인덱스 값
            val index = channelIdArr.indexOf(userData.login)
            val document = documents[index + 1]
            val type = (document["type"] as Long).toInt()

            val linkInfo = LinkInfo(
                id = document["id"] as String,
                url = document["url"] as String,
                explain = document["explain"] as String,
                type = type
            )

            val channelData = if (type == 0) { // 침착맨 채널의 정보일 때
                getTwitchUserState(
                    retrofitService,
                    accessKey,
                    userData,
                    linkInfo
                )
            } else {
                Channel(
                    id = userData.id,
                    name = userData.displayName,
                    explains = arrayOf(linkInfo.explain),
                    url = linkInfo.url,
                    image = userData.profile_image_url,
                    thumbnailImage = userData.offline_image_url,
                    type = linkInfo.type,
                    isOnline = false
                )
            }

            channelList[index] = channelData // 리스트 추가
        }

        return channelList.toList()
    }

    // 침착맨의 팔로워 방송여부 등을 가져옴
    private suspend fun getTwitchUserState(
        retrofitService: RetrofitService,
        accessKey: String,
        userInfo: UserDataDTO,
        linkInfo: LinkInfo
    ): Channel {
        val channelData = CoroutineScope(Dispatchers.IO).async {
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

            Channel(
                id = userInfo.id,
                name = userInfo.displayName,
                explains = arrayOf(linkInfo.explain, followData.total.toString()),
                url = linkInfo.url,
                image = userInfo.profile_image_url,
                thumbnailImage = thumbnailImage,
                type = linkInfo.type,
                isOnline = isOnline
            )
        }.await()

        Log.i("Check", "data: $channelData")
        return channelData
    }
}