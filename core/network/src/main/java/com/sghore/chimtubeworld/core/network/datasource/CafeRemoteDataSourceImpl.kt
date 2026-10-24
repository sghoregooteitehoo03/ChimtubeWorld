package com.sghore.chimtubeworld.core.network.datasource

import com.sghore.chimtubeworld.core.network.di.NaverCafe
import com.sghore.chimtubeworld.core.network.retrofit.NaverRetrofitService
import javax.inject.Inject

internal class CafeRemoteDataSourceImpl @Inject constructor(
    @param:NaverCafe private val service: NaverRetrofitService
) : CafeRemoteDataSource {
    override suspend fun getCafePosts(categoryId: Int, page: Int, limit: Int) =
        service.getCafePosts(categoryId, page, limit)
}
