package com.sghore.chimtubeworld.domain

import com.sghore.chimtubeworld.data.model.Resource
import com.sghore.chimtubeworld.data.model.Video
import com.sghore.chimtubeworld.data.repository.TwitchRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetTwitchVideoUseCase @Inject constructor(
    private val repository: TwitchRepository
) {

    operator fun invoke(videoUrl: String, baseUrl: String): Flow<Resource<Video>> = flow {
        try {
            val accessKey = repository.getTwitchAccessKey()
            val videoData = repository.getVideo(
                url = videoUrl,
                baseUrl = baseUrl,
                accessKey = accessKey
            )
            emit(Resource.Success<Video>(videoData))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(Resource.Error<Video>("데이터를 불러오는데 오류가 발생하였습니다."))
        }
    }
}