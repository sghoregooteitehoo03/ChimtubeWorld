package com.sghore.chimtubeworld.domain

import com.sghore.chimtubeworld.data.model.Resource
import com.sghore.chimtubeworld.data.repository.CafeRepository
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class InsertCafeHistoryUseCase @Inject constructor(
    private val repository: CafeRepository
) {
    operator fun invoke(postId: Int) = flow<Resource<Boolean>> {
        try {
            repository.readPost(postId)
            emit(Resource.Success<Boolean>(true))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(Resource.Error<Boolean>(""))
        }
    }
}