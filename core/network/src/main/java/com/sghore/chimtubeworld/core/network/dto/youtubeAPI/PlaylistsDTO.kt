package com.sghore.chimtubeworld.core.network.dto.youtubeAPI

data class PlaylistsDTO(
    val nextPageToken: String? = null,
    val items: List<PlaylistsItemsDTO>
)
