package com.sghore.chimtubeworld.domain

import com.sghore.chimtubeworld.data.repository.YoutubeRepository
import javax.inject.Inject

class GetVideosUseCase @Inject constructor(private val youtubeRepository: YoutubeRepository) {
    operator fun invoke(channelId: String) =
        youtubeRepository.getVideos(channelId)
}