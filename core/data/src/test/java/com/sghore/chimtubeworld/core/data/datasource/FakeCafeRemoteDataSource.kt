package com.sghore.chimtubeworld.core.data.datasource

import com.sghore.chimtubeworld.core.network.datasource.CafeRemoteDataSource
import com.sghore.chimtubeworld.core.network.dto.naver_cafeAPI.CafePostsDTO
import com.sghore.chimtubeworld.core.network.dto.naver_cafeAPI.ResultDTO

internal class FakeCafeRemoteDataSource(
    var response: CafePostsDTO = CafePostsDTO(ResultDTO(emptyList()))
) : CafeRemoteDataSource {
    var failure: Throwable? = null

    data class Request(val categoryId: Int, val page: Int, val limit: Int)

    private val _requests = mutableListOf<Request>()
    val requests: List<Request>
        get() = _requests.toList()

    override suspend fun getCafePosts(categoryId: Int, page: Int, limit: Int): CafePostsDTO {
        _requests += Request(categoryId, page, limit)
        failure?.let { throw it }
        return response
    }
}
