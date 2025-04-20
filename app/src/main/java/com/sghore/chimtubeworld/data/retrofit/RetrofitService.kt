package com.sghore.chimtubeworld.data.retrofit

import com.sghore.chimtubeworld.BuildConfig
import com.sghore.chimtubeworld.data.retrofit.dto.youtubeAPI.ChannelDTO
import com.sghore.chimtubeworld.data.retrofit.dto.youtubeAPI.PlaylistItemsDTO
import com.sghore.chimtubeworld.data.retrofit.dto.youtubeAPI.PlaylistsDTO
import com.sghore.chimtubeworld.data.retrofit.dto.youtubeAPI.VideosDTO
import retrofit2.Call
import retrofit2.http.*

interface RetrofitService {
    // Youtube
    @GET("channels?key=${BuildConfig.YOUTUBE_API_KEY}&part=snippet")
    suspend fun getYChannelInfo(@Query("id") channelId: Array<String>): ChannelDTO

    @GET("playlistItems?key=${BuildConfig.YOUTUBE_API_KEY}&part=contentDetails&maxResults=20")
    fun getYPlaylistItems(
        @Query("playlistId") playlistId: String,
        @Query("pageToken") pageToken: String? = null
    ): Call<PlaylistItemsDTO>

    @GET("videos?key=${BuildConfig.YOUTUBE_API_KEY}&part=snippet,contentDetails,statistics")
    fun getYVideos(@Query("id") videoId: Array<String>): Call<VideosDTO>

    @GET("playlists?key=${BuildConfig.YOUTUBE_API_KEY}&part=snippet&maxResults=20")
    suspend fun getYPlaylists(
        @Query("channelId") channelId: String?,
        @Query("id") playlistId: List<String>?,
        @Query("pageToken") pageToken: String? = null
    ): PlaylistsDTO
}