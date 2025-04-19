package com.sghore.chimtubeworld.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.sghore.chimtubeworld.data.repository.dataSource.MarpleShopProductPagingSource
import com.sghore.chimtubeworld.data.repository.dataSource.NaverProductPagingSource
import com.sghore.chimtubeworld.data.retrofit.RetrofitService
import com.sghore.chimtubeworld.other.Constants
import retrofit2.Retrofit
import javax.inject.Inject

class StoreRepository @Inject constructor(
    private val retrofitBuilder: Retrofit.Builder
) {

    fun getProductsFromNaver() = Pager(PagingConfig(pageSize = 20)) {
        val retrofitService = getRetrofit(Constants.NAVERSTORE_API_URL)
        NaverProductPagingSource(retrofitService)
    }.flow

    fun getProductsFromMarple() = Pager(PagingConfig(pageSize = 20)) {
        val retrofitService = getRetrofit(Constants.MARPLESHOP_API_URL)
        MarpleShopProductPagingSource(retrofitService)
    }.flow

    private fun getRetrofit(baseUrl: String) =
        retrofitBuilder.baseUrl(baseUrl)
            .build()
            .create(RetrofitService::class.java)
}