package com.sghore.chimtubeworld.core.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.google.firebase.firestore.FirebaseFirestore
import com.sghore.chimtubeworld.core.network.datasource.ChzzkRemoteDataSource
import com.sghore.chimtubeworld.core.database.dao.Dao
import com.sghore.chimtubeworld.core.model.Channel
import com.sghore.chimtubeworld.core.model.LinkInfo
import com.sghore.chimtubeworld.core.model.Video
import com.sghore.chimtubeworld.core.common.Constants
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*
import com.sghore.chimtubeworld.core.domain.repository.TwitchRepository
import javax.inject.Inject
import kotlin.time.Duration

internal class TwitchRepositoryImpl @Inject constructor(
    private val store: FirebaseFirestore,
    private val remoteDataSource: ChzzkRemoteDataSource,
    private val dao: Dao
) : TwitchRepository {

    override suspend fun getChannels(): List<Channel> {
        val channelLinkData = getChannelLinkData()
        return getTwitchUserInfo(channelLinkData)
    }

    // Twitch Id 및 설명을 가져옴
    private suspend fun getChannelLinkData() =
        store.collection(Constants.COLLECTION_TWITCH_LINK)
            .whereNotEqualTo("explain", "")
            .get()
            .await()
            .toObjects(LinkInfo::class.java)

    // 트위치 채널의 정보를 가져옴
    private suspend fun getTwitchUserInfo(
        channelLinkList: List<LinkInfo>,
    ): List<Channel> {
        return channelLinkList.map {
            val channelInfo = remoteDataSource.getChannelInfo(it.id)
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
                        val liveImage = remoteDataSource.getChannelMoreInfo(it.id)
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
}
