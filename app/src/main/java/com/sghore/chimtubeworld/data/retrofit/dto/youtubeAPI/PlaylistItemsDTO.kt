package com.sghore.chimtubeworld.data.retrofit.dto.youtubeAPI

data class PlaylistItemsDTO(
    val nextPageToken: String,
    val prevPageToken: String? = null,
    val items: List<PlaylistItemDTO>
)