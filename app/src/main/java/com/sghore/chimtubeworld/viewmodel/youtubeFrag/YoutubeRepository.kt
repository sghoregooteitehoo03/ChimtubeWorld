package com.sghore.chimtubeworld.viewmodel.youtubeFrag

import com.google.firebase.firestore.FirebaseFirestore
import com.sghore.chimtubeworld.data.Channel
import com.sghore.chimtubeworld.data.LinkInfo
import com.sghore.chimtubeworld.other.Contents
import com.sghore.chimtubeworld.retrofit.RetrofitService
import kotlinx.coroutines.tasks.await
import retrofit2.Retrofit
import retrofit2.await
import javax.inject.Inject

class YoutubeRepository @Inject constructor(
    private val store: FirebaseFirestore,
    private val retrofitBuilder: Retrofit.Builder
) {

    // 채널의 정보를 가져옴
    suspend fun getChannelInfo(): MutableList<Channel> {
        val retrofitService = retrofitBuilder.baseUrl(Contents.YOUTUBE_API_URL)
            .build()
            .create(RetrofitService::class.java)
        val channelList = mutableListOf<Channel>() // 채널 리스트
        // 채널의 Id 및 API에서 가져오지 못하는 부가설명을 가져옴
        val documents = store.collection(Contents.COLLECTION_YOUTUBE_LINK)
            .get()
            .await()
            .documents

        documents.forEach { document ->
            val linkInfo = LinkInfo(
                id = document["id"] as String,
                url = document["url"] as String,
                explain = document["explain"] as String,
                type = (document["type"] as Long).toInt()
            )

            // API를 통해 채널의 정보를 가져옴
            val result = retrofitService.getYChannelInfo(linkInfo.id)
                ?.await()
                ?: throw NullPointerException() // API 실패 시

            val snippet = result.items[0].snippet
            val explain = if (linkInfo.type == 0) {
                snippet.description
            } else {
                linkInfo.explain
            }

            val channelData = Channel(
                name = snippet.title,
                explains = arrayOf(explain),
                url = linkInfo.url,
                image = snippet.thumbnails.medium.url,
                type = linkInfo.type
            )

            channelList.add(channelData)
        }

        return channelList
    }
}