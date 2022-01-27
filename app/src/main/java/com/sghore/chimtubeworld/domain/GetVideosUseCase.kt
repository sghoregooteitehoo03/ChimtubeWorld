package com.sghore.chimtubeworld.domain

import com.sghore.chimtubeworld.R
import com.sghore.chimtubeworld.data.repository.TwitchRepository
import com.sghore.chimtubeworld.data.repository.YoutubeRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetVideosUseCase @Inject constructor(
    private val youtubeRepository: YoutubeRepository,
    private val twitchRepository: TwitchRepository
) {
    operator fun invoke(typeImageRes: Int, channelId: String) =
        if (typeImageRes == R.drawable.youtube) {
            youtubeRepository.getVideos(channelId)
        } else {
            twitchRepository.getVideos(channelId)
        }
}