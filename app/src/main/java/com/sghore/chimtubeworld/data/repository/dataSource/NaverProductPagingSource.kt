package com.sghore.chimtubeworld.data.repository.dataSource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.sghore.chimtubeworld.data.model.Goods
import com.sghore.chimtubeworld.data.retrofit.RetrofitService
import com.sghore.chimtubeworld.other.Contents

class NaverProductPagingSource(
    private val retrofitService: RetrofitService
) : PagingSource<Int, Goods>() {
    override fun getRefreshKey(state: PagingState<Int, Goods>): Int? {
        return null
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Goods> {
        return try {
            val key = params.key ?: 1

            val productsData = retrofitService.getProductsFromNaver(key)
            if (productsData.simpleProducts.isEmpty()) {
                throw IndexOutOfBoundsException()
            }
1
            val goods = productsData.simpleProducts.map {
                Goods(
                    title = it.name,
                    price = it.salePrice,
                    thumbnailImage = it.representativeImageUrl,
                    previewImages = listOf(it.representativeImageUrl) + it.optionalImageUrls,
                    url = Contents.NAVERSTORE_BASE_URL + it.id
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

    private fun getProducts() {

    }
}