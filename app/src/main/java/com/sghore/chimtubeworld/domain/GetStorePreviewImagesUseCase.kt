package com.sghore.chimtubeworld.domain

import com.sghore.chimtubeworld.data.model.Goods
import com.sghore.chimtubeworld.data.model.Resource
import com.sghore.chimtubeworld.data.repository.StoreRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetStorePreviewImagesUseCase @Inject constructor(
    private val repository: StoreRepository
) {
    operator fun invoke(goods: Goods): Flow<Resource<List<String>>> = flow {
        try {
            val previewImages = CoroutineScope(Dispatchers.IO).async {
                repository.getStorePreviewImages(goods)
            }.await()
            emit(Resource.Success(previewImages))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(Resource.Error("데이터를 불러오는데 오류가 발생하였습니다."))
        }
    }
}