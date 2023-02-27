package com.sghore.chimtubeworld.domain

import com.sghore.chimtubeworld.data.model.Channel
import com.sghore.chimtubeworld.data.model.Goods
import com.sghore.chimtubeworld.data.model.GoodsElement
import com.sghore.chimtubeworld.data.model.Resource
import com.sghore.chimtubeworld.data.repository.StoreRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetGoodsElementUseCase @Inject constructor(
    private val repository: StoreRepository
) {
    operator fun invoke(): Flow<Resource<List<GoodsElement>>> = flow {
        try {
            val goodsElement = repository.getGoodsElement()
            emit(Resource.Success(goodsElement))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(Resource.Error("데이터를 불러오는데 오류가 발생하였습니다."))
        }
    }
}