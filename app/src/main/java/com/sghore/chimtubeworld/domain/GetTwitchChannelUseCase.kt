package com.sghore.chimtubeworld.domain

import com.sghore.chimtubeworld.data.model.Channel
import com.sghore.chimtubeworld.data.model.Resource
import com.sghore.chimtubeworld.data.repository.TwitchRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.lang.Exception
import javax.inject.Inject

class GetTwitchChannelUseCase @Inject constructor(
    private val repository: TwitchRepository
) {
    operator fun invoke(): Flow<Resource<List<Channel?>>> = flow {
        try {
            emit(Resource.Loading<List<Channel?>>())
            val channels = repository.getTwitchUserInfo()
            emit(Resource.Success<List<Channel?>>(channels))
        } catch (e: Exception) {
            emit(Resource.Error<List<Channel?>>("데이터를 불러오는데 오류가 발생하였습니다."))
        }
    }
}