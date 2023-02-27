package com.sghore.chimtubeworld.domain

import com.sghore.chimtubeworld.data.model.Goods
import com.sghore.chimtubeworld.data.model.GoodsElement
import com.sghore.chimtubeworld.data.repository.StoreRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetGoodsListUseCase @Inject constructor(
    private val repository: StoreRepository
) {
    operator fun invoke(goodsElement: GoodsElement?): Flow<List<Goods>> = flow {
        try {
            if (goodsElement == null) {
                throw NullPointerException()
            }
            emit(repository.getGoodsList(goodsElement))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(emptyList())
        }
    }
}