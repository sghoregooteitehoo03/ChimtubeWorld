package com.sghore.chimtubeworld.data.retrofit

import com.sghore.chimtubeworld.data.retrofit.dto.naver_cafeAPI.CafePostsDTO
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface CafeRetrofitService {
    @GET("cafe-web/cafe-boardlist-api/v1/cafes/29646865/menus/{categoryId}/articles")
    suspend fun getCafePosts(
        @Path("categoryId") categoryId: Int,
        @Query("page") page: Int,
        @Query("pageSize") limit: Int = 10,
    ): CafePostsDTO
}