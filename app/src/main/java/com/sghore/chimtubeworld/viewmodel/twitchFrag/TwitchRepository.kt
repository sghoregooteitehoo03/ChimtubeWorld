package com.sghore.chimtubeworld.viewmodel.twitchFrag

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.sghore.chimtubeworld.data.Channel
import com.sghore.chimtubeworld.data.LinkInfo
import com.sghore.chimtubeworld.other.Contents
import com.sghore.chimtubeworld.retrofit.RetrofitService
import com.sghore.chimtubeworld.retrofit.dto.twitchAPI.UserDataDTO
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.tasks.await
import retrofit2.Retrofit
import retrofit2.await
import javax.inject.Inject
import kotlin.NullPointerException


class TwitchRepository @Inject constructor(
    private val store: FirebaseFirestore,
    private val retrofitBuilder: Retrofit.Builder
) {

    // 트위치 채널의 정보를 가져옴
    suspend fun getTwitchUserInfo(): List<Channel> {
        var accessKey = "" // API 액세스 키
        val retrofitService = retrofitBuilder.baseUrl(Contents.TWITCH_API_URL)
            .build()
            .create(RetrofitService::class.java)
        val channelList = mutableListOf<Channel>()
        // Twitch Id 및 설명을 가져옴
        val documents = store.collection(Contents.COLLECTION_TWITCH_LINK)
            .get()
            .await()
            .documents

        documents.forEach { document ->
            val type = (document["type"] as Long).toInt()

            if (type != -1) { // ACCESS KEY 가져온 후
                val linkInfo = LinkInfo(
                    id = document["id"] as String,
                    url = document["url"] as String,
                    explain = document["explain"] as String,
                    type = (document["type"] as Long).toInt()
                )
                // Twitch API를 통해 채널의 정보를 가져옴
                val result = retrofitService.getTUserInfo(accessKey, linkInfo.id)
                    ?.await()
                    ?: throw NullPointerException() // API 실패 시

                // API 요청 성공
                val userInfo = result.data[0]
                val channelData = if (type == 0) {
                    getTwitchUserState(
                        retrofitService,
                        accessKey,
                        userInfo,
                        linkInfo
                    )
                } else {
                    Channel(
                        id = userInfo.id,
                        name = userInfo.displayName,
                        explains = arrayOf(linkInfo.explain),
                        url = linkInfo.url,
                        image = userInfo.profile_image_url,
                        thumbnailImage = userInfo.offline_image_url,
                        type = linkInfo.type,
                        isOnline = false
                    )
                }

                channelList.add(channelData) // 리스트 추가
            } else { // ACCESS KEY 가져오기 전
                accessKey = "Bearer ${document["AUTH"] as String}"
            }

        }

        return channelList
    }

    private suspend fun getTwitchUserState(
        retrofitService: RetrofitService,
        accessKey: String,
        userInfo: UserDataDTO,
        linkInfo: LinkInfo
    ): Channel {
        val channelData = CoroutineScope(Dispatchers.IO).async {
            val followData = retrofitService.getTUserFollows(accessKey, userInfo.id)
                ?.await()
                ?: throw NullPointerException() // API 실패 시
            val streamData = retrofitService.getTUserStream(accessKey, userInfo.login)
                ?.await()
                ?: throw NullPointerException() // API 실패 시
            val isOnline: Boolean

            val thumbnailImage = if (streamData.data.isEmpty()) {
                isOnline = false
                userInfo.offline_image_url
            } else {
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