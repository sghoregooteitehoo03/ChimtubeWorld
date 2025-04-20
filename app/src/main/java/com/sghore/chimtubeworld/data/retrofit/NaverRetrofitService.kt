package com.sghore.chimtubeworld.data.retrofit

import com.sghore.chimtubeworld.data.retrofit.dto.naver_cafeAPI.CafePostsDTO
import com.sghore.chimtubeworld.data.retrofit.dto.naver_chzzkAPI.ChannelDTO
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface NaverRetrofitService {
    @GET("cafe-web/cafe-boardlist-api/v1/cafes/29646865/menus/{categoryId}/articles")
    suspend fun getCafePosts(
        @Path("categoryId") categoryId: Int,
        @Query("page") page: Int,
        @Query("pageSize") limit: Int = 10,
    ): CafePostsDTO

    @GET("v1/channels/{channelId}")
    suspend fun getChannelInfo(
        @Path("channelId") channelId: String
    ): ChannelDTO

    @GET("v1/channels/{channelId}/data")
    suspend fun getChannelMoreInfo(
        @Path("channelId") channelId: String,
        @Query("fields") fields: String = "topExposedVideos",
    ): ChannelDTO
}