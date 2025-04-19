package com.sghore.chimtubeworld.data.repository.dataSource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.sghore.chimtubeworld.data.model.Goods
import com.sghore.chimtubeworld.data.retrofit.RetrofitService
import com.sghore.chimtubeworld.other.Constants

class MarpleShopProductPagingSource(
    private val retrofitService: RetrofitService
) : PagingSource<Int, Goods>() {
    override fun getRefreshKey(state: PagingState<Int, Goods>): Int? {
        return null
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Goods> {
        return try {
            val key = params.key ?: 0

            val productsData = retrofitService.getProductsFromMarple(key)
            val goods = productsData.map {
                Goods(
                    title = it.productInfo.baseProduct.name ?: it.name,
                    price = if (it.price != 0) it.price * 10 else it.profit,
                    thumbnailImage = "https://" + it.thumbnails.value[0].url,
                    previewImages = it.thumbnails.value.map { "https://" + it.url },
                    url = Constants.MARPLESHOP_BASE_URL + it.id
                )
            }

            LoadResult.Page(
                data = goods,
                prevKey = null,
                nextKey = if (productsData.isNotEmpty()) {
                    key + 1
                } else {
                    null
                }
            )
        } catch (e: Exception) {
            e.printStackTrace()
            LoadResult.Error(e)
        }
    }
}