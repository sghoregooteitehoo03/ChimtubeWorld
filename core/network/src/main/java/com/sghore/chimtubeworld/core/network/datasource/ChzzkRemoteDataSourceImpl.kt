package com.sghore.chimtubeworld.core.network.datasource

import com.sghore.chimtubeworld.core.network.di.Chzzk
import com.sghore.chimtubeworld.core.network.retrofit.NaverRetrofitService
import javax.inject.Inject

internal class ChzzkRemoteDataSourceImpl @Inject constructor(
    @param:Chzzk private val service: NaverRetrofitService
) : ChzzkRemoteDataSource {
    override suspend fun getChannelInfo(channelId: String) =
        service.getChannelInfo(channelId)

    override suspend fun getChannelMoreInfo(channelId: String, fields: String) =
        service.getChannelMoreInfo(channelId, fields)
}
