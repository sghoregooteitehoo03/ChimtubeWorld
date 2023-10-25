package com.sghore.chimtubeworld.data.repository.dataSource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.sghore.chimtubeworld.data.model.Goods
import com.sghore.chimtubeworld.data.retrofit.RetrofitService
import com.sghore.chimtubeworld.other.Contents

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
            if (productsData.isEmpty()) {
                throw IndexOutOfBoundsException()
            }

            val goods = productsData.map {
                Goods(
                    title = it.productInfo.baseProduct.name,
                    price = it.price * 10,
                    thumbnailImage = "https://" + it.thumbnails.value[0].url,
                    previewImages = it.thumbnails.value.map { "https://" + it.url },
                    url = Contents.MARPLESHOP_BASE_URL + it.id
                )
            }

            LoadResult.Page(
                data = goods,
                prevKey = null,
                nextKey = key + 1
            )
        } catch (e: Exception) {
            e.printStackTrace()
            LoadResult.Error(e)
        }
    }
}