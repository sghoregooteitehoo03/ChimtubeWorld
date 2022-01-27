package com.sghore.chimtubeworld.domain

import com.sghore.chimtubeworld.data.model.Channel
import com.sghore.chimtubeworld.data.model.Resource
import com.sghore.chimtubeworld.data.repository.WebToonRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.lang.Exception
import javax.inject.Inject

class GetWebToonListUseCase @Inject constructor(
    private val repository: WebToonRepository
) {
    operator fun invoke(): Flow<Resource<List<Channel>>> = flow {
        try {
            emit(Resource.Loading<List<Channel>>())
            val webtoons = CoroutineScope(Dispatchers.IO).async {
                repository.getWebToonList()
            }.await()
            emit(Resource.Success<List<Channel>>(webtoons))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(Resource.Error<List<Channel>>("데이터를 불러오는데 오류가 발생하였습니다."))
        }
    }
}