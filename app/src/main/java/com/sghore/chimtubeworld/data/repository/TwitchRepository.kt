package com.sghore.chimtubeworld.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.google.firebase.firestore.FirebaseFirestore
import com.sghore.chimtubeworld.data.db.Dao
import com.sghore.chimtubeworld.data.model.Channel
import com.sghore.chimtubeworld.data.model.LinkInfo
import com.sghore.chimtubeworld.data.model.Video
import com.sghore.chimtubeworld.data.retrofit.NaverRetrofitService
import com.sghore.chimtubeworld.other.Constants
import com.sghore.chimtubeworld.data.retrofit.RetrofitService
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

    // Twitch Id 및 설명을 가져옴
    suspend fun getChannelLinkData() =
        store.collection(Constants.COLLECTION_TWITCH_LINK)
            .whereNotEqualTo("explain", "")
            .get()
            .await()
            .toObjects(LinkInfo::class.java)

    // 트위치 채널의 정보를 가져옴
    suspend fun getTwitchUserInfo(
        channelLinkList: List<LinkInfo>,
    ): List<Channel> {
        val retrofitService = getRetrofit()

        return channelLinkList.map {
            val channelInfo = retrofitService.getChannelInfo(it.id)
            val isLive = channelInfo.content.openLive
            Channel(
                id = it.id,
                playlistId = it.id,
                playlistName = it.playlistName,
                name = channelInfo.content.channelName,
                explains = arrayOf(it.explain, channelInfo.content.followerCount.toString()),
                url = it.url,
                image = channelInfo.content.channelImageUrl,
                thumbnailImage = if (it.type == 0) {
                    if (isLive) {
                        val liveImage = retrofitService.getChannelMoreInfo(it.id)
                            .content.topExposedVideos?.openLive?.liveImageUrl ?: ""
                        liveImage.replace("{type}", "1080")
                    } else {
                        "https://static-cdn.jtvnw.net/jtv_user_pictures/zilioner-channel_offline_image-442244d67f3c4714-1920x1080.png"
                    }
                } else {
                    ""
                },
                type = it.type,
                isOnline = isLive,
            )
        }
    }

    private fun getRetrofit() =
        retrofitBuilder.baseUrl(Constants.NAVER_CHZZK_API_URL)
            .build()
            .create(NaverRetrofitService::class.java)
}