package com.sghore.chimtubeworld.data.retrofit

import com.sghore.chimtubeworld.other.Contents
import com.sghore.chimtubeworld.data.retrofit.dto.twitchAPI.StreamDTO
import com.sghore.chimtubeworld.data.retrofit.dto.twitchAPI.UserDTO
import com.sghore.chimtubeworld.data.retrofit.dto.twitchAPI.UserFollowsDTO
import com.sghore.chimtubeworld.data.retrofit.dto.youtubeAPI.ChannelDTO
import com.sghore.chimtubeworld.data.retrofit.dto.youtubeAPI.PlaylistItemsDTO
import com.sghore.chimtubeworld.data.retrofit.dto.youtubeAPI.PlaylistsDTO
import com.sghore.chimtubeworld.data.retrofit.dto.youtubeAPI.VideosDTO
import retrofit2.Call
import retrofit2.http.*

interface RetrofitService {
    // Youtube
    @GET("channels?key=${Contents.API_KEY}&part=snippet")
    suspend fun getYChannelInfo(@Query("id") channelId: Array<String>): ChannelDTO

    @GET("playlistItems?key=${Contents.API_KEY}&part=contentDetails&maxResults=20")
    fun getYPlaylistItems(
        @Query("playlistId") playlistId: String,
        @Query("pageToken") pageToken: String? = null
    ): Call<PlaylistItemsDTO>

    @GET("videos?key=${Contents.API_KEY}&part=snippet,contentDetails,statistics")
    fun getYVideos(@Query("id") videoId: Array<String>): Call<VideosDTO>

    @GET("playlists?key=${Contents.API_KEY}&part=snippet&maxResults=20")
    suspend fun getYPlaylists(
        @Query("channelId") channelId: String?,
        @Query("id") playlistId: List<String>?,
        @Query("pageToken") pageToken: String? = null
    ): PlaylistsDTO

    // Twitch
    @Headers("Client-ID: ${Contents.CLIENT_ID}")
    @GET("users")
    suspend fun getTUserInfo(
        @Header("Authorization") accessKey: String,
        @Query("login") loginId: Array<String>
    ): UserDTO

    @Headers("Client-ID: ${Contents.CLIENT_ID}")
    @GET("users/follows?first=1")
    suspend fun getTUserFollows(
        @Header("Authorization") accessKey: String,
        @Query("to_id") id: String,
    ): UserFollowsDTO

    @Headers("Client-ID: ${Contents.CLIENT_ID}")
    @GET("streams")
    suspend fun getTUserStream(
        @Header("Authorization") accessKey: String,
        @Query("user_login") loginId: String,
    ): StreamDTO

    @Headers("Client-ID: ${Contents.CLIENT_ID}")
    @GET("videos")
    fun getTVideosFromUserId(
        @Header("Authorization") accessKey: String,
        @Query("user_id") userId: String,
        @Query("after") afterPage: String
    ): Call<com.sghore.chimtubeworld.data.retrofit.dto.twitchAPI.VideosDTO>

    @Headers("Client-ID: ${Contents.CLIENT_ID}")
    @GET("videos")
    fun getTVideoFromVideoId(
        @Header("Authorization") accessKey: String,
        @Query("id") videoId: String
    ): Call<com.sghore.chimtubeworld.data.retrofit.dto.twitchAPI.VideosDTO>
}