package com.sghore.chimtubeworld.core.network.datasource

import com.sghore.chimtubeworld.core.network.dto.naver_chzzkAPI.ChannelDTO

interface ChzzkRemoteDataSource {
    suspend fun getChannelInfo(channelId: String): ChannelDTO
    suspend fun getChannelMoreInfo(channelId: String, fields: String = "topExposedVideos"): ChannelDTO
}
