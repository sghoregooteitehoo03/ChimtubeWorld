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
    suspend fun getChannelInfo(): MutableList<Channel?> {
        val retrofitService = retrofitBuilder.baseUrl(Contents.YOUTUBE_API_URL)
            .build()
            .create(RetrofitService::class.java)
        val channelList = arrayOfNulls<Channel>(7)
            .toMutableList() // 채널 리스트
        // 채널의 Id 및 API에서 가져오지 못하는 부가설명을 가져옴
        val documents = store.collection(Contents.COLLECTION_YOUTUBE_LINK)
            .get()
            .await()
            .documents
        // 채널 아이디 배열            [0] = 채널아이디, [1] = 플레이리스트 아이디
        val channelIdArr = documents.map { (it["id"] as String).split("|")[0] }
            .toTypedArray()

        // API를 통해 채널들의 정보를 가져옴
        val result = retrofitService.getYChannelInfo(channelIdArr)
            .await()

        result.items.forEach { channelInfo ->
            // 채널들을 배열순서에 맞쳐 리스트에 집어넣기 위한 인덱스 값
            val index = channelIdArr.indexOf(channelInfo.id)
            val document = documents[index]
            val linkInfo = LinkInfo(
                id = document["id"] as String,
                url = document["url"] as String,
                explain = document["explain"] as String,
                type = (document["type"] as Long).toInt()
            )
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

        return channelList
    }
}