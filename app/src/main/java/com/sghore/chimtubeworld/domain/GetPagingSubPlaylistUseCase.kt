package com.sghore.chimtubeworld.domain

import com.sghore.chimtubeworld.data.repository.YoutubeRepository
import javax.inject.Inject

class GetPagingSubPlaylistUseCase @Inject constructor(
    private val repository: YoutubeRepository
) {

    operator fun invoke(
        playlistId: List<String>
    ) = repository.getPagingSubPlaylist(
        playlistId = playlistId
    )
}