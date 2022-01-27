package com.sghore.chimtubeworld.domain

import com.sghore.chimtubeworld.data.model.Goods
import com.sghore.chimtubeworld.data.repository.StoreRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetGoodsListUseCase @Inject constructor(
    private val repository: StoreRepository
) {
    operator fun invoke(selectedGoodsUrl: String): Flow<List<Goods>> = flow {
        try {
            if (selectedGoodsUrl.isEmpty()) {
                throw NullPointerException()
            }
            emit(emptyList())
            emit(
                CoroutineScope(Dispatchers.IO).async {
                    repository.getGoodsList(selectedGoodsUrl)
                }.await()
            )
        } catch (e: Exception) {
            e.printStackTrace()
            emit(emptyList())
        }
    }
}