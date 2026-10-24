package com.sghore.chimtubeworld.core.data.datasource

import com.sghore.chimtubeworld.core.network.datasource.YoutubeRemoteDataSource
import com.sghore.chimtubeworld.core.network.dto.youtubeAPI.ChannelDTO
import com.sghore.chimtubeworld.core.network.dto.youtubeAPI.PlaylistItemsDTO
import com.sghore.chimtubeworld.core.network.dto.youtubeAPI.PlaylistsDTO
import com.sghore.chimtubeworld.core.network.dto.youtubeAPI.VideosDTO

internal class FakeYoutubeRemoteDataSource : YoutubeRemoteDataSource {
    var channelResponse = ChannelDTO(emptyList())
    var playlistItemsResponse = PlaylistItemsDTO(nextPageToken = "", items = emptyList())
    var videosResponse = VideosDTO(emptyList())
    var playlistsResponse = PlaylistsDTO(items = emptyList())
    var channelFailure: Throwable? = null
    var playlistItemsFailure: Throwable? = null
    var videosFailure: Throwable? = null
    var playlistsFailure: Throwable? = null

    data class PlaylistItemsRequest(val playlistId: String, val pageToken: String?)
    data class PlaylistsRequest(
        val channelId: String?,
        val playlistId: List<String>?,
        val pageToken: String?
    )

    private val _channelRequests = mutableListOf<List<String>>()
    val channelRequests: List<List<String>>
        get() = _channelRequests.toList()
    private val _playlistItemsRequests = mutableListOf<PlaylistItemsRequest>()
    val playlistItemsRequests: List<PlaylistItemsRequest>
        get() = _playlistItemsRequests.toList()
    private val _videoRequests = mutableListOf<List<String>>()
    val videoRequests: List<List<String>>
        get() = _videoRequests.toList()
    private val _playlistsRequests = mutableListOf<PlaylistsRequest>()
    val playlistsRequests: List<PlaylistsRequest>
        get() = _playlistsRequests.toList()

    override suspend fun getYChannelInfo(channelId: Array<String>): ChannelDTO {
        _channelRequests += listOf(channelId.toList())
        channelFailure?.let { throw it }
        return channelResponse
    }

    override suspend fun getYPlaylistItems(playlistId: String, pageToken: String?): PlaylistItemsDTO {
        _playlistItemsRequests += PlaylistItemsRequest(playlistId, pageToken)
        playlistItemsFailure?.let { throw it }
        return playlistItemsResponse
    }

    override suspend fun getYVideos(videoId: Array<String>): VideosDTO {
        _videoRequests += listOf(videoId.toList())
        videosFailure?.let { throw it }
        return videosResponse
    }

    override suspend fun getYPlaylists(
        channelId: String?,
        playlistId: List<String>?,
        pageToken: String?
    ): PlaylistsDTO {
        _playlistsRequests += PlaylistsRequest(channelId, playlistId?.toList(), pageToken)
        playlistsFailure?.let { throw it }
        return playlistsResponse
    }
}
