package com.sghore.chimtubeworld.domain

import com.sghore.chimtubeworld.data.repository.YoutubeRepository
import javax.inject.Inject

class GetPlaylistsUseCase @Inject constructor(
    private val repository: YoutubeRepository
) {

    operator fun invoke(
        channelId: String?,
        playlistId: List<String>
    ) = repository.getPlaylists(
        channelId = channelId,
        playlistId = playlistId
    )
}