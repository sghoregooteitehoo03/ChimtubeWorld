package com.sghore.chimtubeworld.core.domain.repository

import androidx.paging.PagingData
import com.sghore.chimtubeworld.core.model.Channel
import com.sghore.chimtubeworld.core.model.Playlist
import com.sghore.chimtubeworld.core.model.Video
import kotlinx.coroutines.flow.Flow

interface YoutubeRepository {
    fun getVideos(channelId: String): Flow<PagingData<Video>>
    fun getPagingMainPlaylist(channelId: String?, playlistId: List<String>): Flow<PagingData<Playlist>>
    fun getPagingSubPlaylist(playlistId: List<String>): Flow<PagingData<Playlist>>
    suspend fun getChannels(): List<Channel?>
    suspend fun getVideo(url: String): Video
}
