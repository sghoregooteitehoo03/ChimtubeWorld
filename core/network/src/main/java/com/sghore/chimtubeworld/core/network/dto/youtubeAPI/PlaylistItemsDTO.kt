package com.sghore.chimtubeworld.core.network.dto.youtubeAPI

data class PlaylistItemsDTO(
    val nextPageToken: String,
    val prevPageToken: String? = null,
    val items: List<PlaylistItemDTO>
)