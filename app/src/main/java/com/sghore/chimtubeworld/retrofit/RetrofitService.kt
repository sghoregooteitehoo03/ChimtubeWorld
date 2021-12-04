package com.sghore.chimtubeworld.retrofit

import com.sghore.chimtubeworld.other.Contents
import com.sghore.chimtubeworld.retrofit.dto.ChannelDTO
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface RetrofitService {
    @GET("channels?key=${Contents.API_KEY}&part=snippet")
    fun getChannelInfo(@Query("id") channelId: String): Call<ChannelDTO>?
}