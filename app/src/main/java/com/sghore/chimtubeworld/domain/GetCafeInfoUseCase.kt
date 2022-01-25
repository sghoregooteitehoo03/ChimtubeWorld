package com.sghore.chimtubeworld.domain

import com.sghore.chimtubeworld.data.model.Channel
import com.sghore.chimtubeworld.data.model.Resource
import com.sghore.chimtubeworld.data.repository.CafeRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetCafeInfoUseCase @Inject constructor(
    private val repository: CafeRepository
) {
    operator fun invoke(): Flow<Resource<Channel>> = flow {
        try {
            val cafeInfo = CoroutineScope(Dispatchers.IO).async {
                repository.getCafeInfo()
            }.await()

            emit(Resource.Success<Channel>(cafeInfo))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(Resource.Error<Channel>("데이터를 불러오는데 오류가 발생하였습니다."))
        }
    }
}