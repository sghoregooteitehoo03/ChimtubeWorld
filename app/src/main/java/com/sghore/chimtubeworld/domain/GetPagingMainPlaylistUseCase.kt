package com.sghore.chimtubeworld.domain

import com.sghore.chimtubeworld.data.repository.YoutubeRepository
import javax.inject.Inject

class GetPagingMainPlaylistUseCase @Inject constructor(
    private val repository: YoutubeRepository
) {
    operator fun invoke(
        channelId: String?,
        playlistId: List<String>
    ) = repository.getPagingMainPlaylist(
        channelId = channelId,
        playlistId = playlistId
    )
}