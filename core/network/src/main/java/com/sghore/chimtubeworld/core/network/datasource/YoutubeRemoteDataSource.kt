package com.sghore.chimtubeworld.core.network.datasource

import com.sghore.chimtubeworld.core.network.dto.youtubeAPI.ChannelDTO
import com.sghore.chimtubeworld.core.network.dto.youtubeAPI.PlaylistItemsDTO
import com.sghore.chimtubeworld.core.network.dto.youtubeAPI.PlaylistsDTO
import com.sghore.chimtubeworld.core.network.dto.youtubeAPI.VideosDTO

interface YoutubeRemoteDataSource {
    suspend fun getYChannelInfo(channelId: Array<String>): ChannelDTO
    suspend fun getYPlaylistItems(playlistId: String, pageToken: String? = null): PlaylistItemsDTO
    suspend fun getYVideos(videoId: Array<String>): VideosDTO
    suspend fun getYPlaylists(
        channelId: String?,
        playlistId: List<String>?,
        pageToken: String? = null
    ): PlaylistsDTO
}
