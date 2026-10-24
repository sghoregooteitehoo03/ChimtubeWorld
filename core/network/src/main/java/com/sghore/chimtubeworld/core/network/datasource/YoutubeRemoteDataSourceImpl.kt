package com.sghore.chimtubeworld.core.network.datasource

import com.sghore.chimtubeworld.core.network.retrofit.RetrofitService
import retrofit2.await
import javax.inject.Inject

internal class YoutubeRemoteDataSourceImpl @Inject constructor(
    private val service: RetrofitService
) : YoutubeRemoteDataSource {
    override suspend fun getYChannelInfo(channelId: Array<String>) =
        service.getYChannelInfo(channelId)

    override suspend fun getYPlaylistItems(playlistId: String, pageToken: String?) =
        service.getYPlaylistItems(playlistId, pageToken).await()

    override suspend fun getYVideos(videoId: Array<String>) =
        service.getYVideos(videoId).await()

    override suspend fun getYPlaylists(channelId: String?, playlistId: List<String>?, pageToken: String?) =
        service.getYPlaylists(channelId, playlistId, pageToken)
}
