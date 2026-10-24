package com.sghore.chimtubeworld.core.network.datasource

import com.sghore.chimtubeworld.core.network.dto.naver_cafeAPI.CafePostsDTO

interface CafeRemoteDataSource {
    suspend fun getCafePosts(categoryId: Int, page: Int, limit: Int = 10): CafePostsDTO
}
