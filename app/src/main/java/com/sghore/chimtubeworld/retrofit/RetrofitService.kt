package com.sghore.chimtubeworld.retrofit

import com.sghore.chimtubeworld.other.Contents
import com.sghore.chimtubeworld.retrofit.dto.twitchAPI.StreamDTO
import com.sghore.chimtubeworld.retrofit.dto.twitchAPI.StreamDataDTO
import com.sghore.chimtubeworld.retrofit.dto.twitchAPI.UserDTO
import com.sghore.chimtubeworld.retrofit.dto.twitchAPI.UserFollowsDTO
import com.sghore.chimtubeworld.retrofit.dto.youtubeAPI.ChannelDTO
import com.sghore.chimtubeworld.retrofit.dto.youtubeAPI.PlaylistItemsDTO
import com.sghore.chimtubeworld.retrofit.dto.youtubeAPI.VideosDTO
import retrofit2.Call
import retrofit2.http.*

interface RetrofitService {
    // Youtube
    @GET("channels?key=${Contents.API_KEY}&part=snippet")
    fun getYChannelInfo(@Query("id") channelId: Array<String>): Call<ChannelDTO>

    @GET("playlistItems?key=${Contents.API_KEY}&part=contentDetails&maxResults=20")
    fun getYPlaylistItems(
        @Query("playlistId") channelId: String,
        @Query("pageToken") pageToken: String? = null
    ): Call<PlaylistItemsDTO>

    @GET("videos?key=${Contents.API_KEY}&part=snippet,contentDetails,statistics")
    fun getYVideos(@Query("id") videoId: Array<String>): Call<VideosDTO>

    // Twitch
    @Headers("Client-ID: ${Contents.CLIENT_ID}")
    @GET("users")
    fun getTUserInfo(
        @Header("Authorization") accessKey: String,
        @Query("login") loginId: Array<String>
    ): Call<UserDTO>

    @Headers("Client-ID: ${Contents.CLIENT_ID}")
    @GET("users/follows?first=1")
    fun getTUserFollows(
        @Header("Authorization") accessKey: String,
        @Query("to_id") id: String,
    ): Call<UserFollowsDTO>

    @Headers("Client-ID: ${Contents.CLIENT_ID}")
    @GET("streams")
    fun getTUserStream(
        @Header("Authorization") accessKey: String,
        @Query("user_login") loginId: String,
    ): Call<StreamDTO>

    @Headers("Client-ID: ${Contents.CLIENT_ID}")
    @GET("videos")
    fun getTVideos(
        @Header("Authorization") accessKey: String,
        @Query("user_id") userId: String,
        @Query("after") afterPage: String?
    ): Call<com.sghore.chimtubeworld.retrofit.dto.twitchAPI.VideosDTO>
}