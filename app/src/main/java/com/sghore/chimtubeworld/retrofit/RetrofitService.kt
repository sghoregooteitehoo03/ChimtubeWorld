package com.sghore.chimtubeworld.retrofit

import com.sghore.chimtubeworld.other.Contents
import com.sghore.chimtubeworld.retrofit.dto.twitchAPI.StreamDTO
import com.sghore.chimtubeworld.retrofit.dto.twitchAPI.StreamDataDTO
import com.sghore.chimtubeworld.retrofit.dto.twitchAPI.UserDTO
import com.sghore.chimtubeworld.retrofit.dto.twitchAPI.UserFollowsDTO
import com.sghore.chimtubeworld.retrofit.dto.youtubeAPI.ChannelDTO
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Query

interface RetrofitService {
    @GET("channels?key=${Contents.API_KEY}&part=snippet")
    fun getYChannelInfo(@Query("id") channelId: String): Call<ChannelDTO>?

    @Headers("Client-ID: ${Contents.CLIENT_ID}")
    @GET("users")
    fun getTUserInfo(
        @Header("Authorization") accessKey: String,
        @Query("login") loginId: String
    ): Call<UserDTO>?

    @Headers("Client-ID: ${Contents.CLIENT_ID}")
    @GET("users/follows?first=1")
    fun getTUserFollows(
        @Header("Authorization") accessKey: String,
        @Query("to_id") id: String,
    ): Call<UserFollowsDTO>?

    @Headers("Client-ID: ${Contents.CLIENT_ID}")
    @GET("streams")
    fun getTUserStream(
        @Header("Authorization") accessKey: String,
        @Query("user_login") loginId: String,
    ): Call<StreamDTO>?
}