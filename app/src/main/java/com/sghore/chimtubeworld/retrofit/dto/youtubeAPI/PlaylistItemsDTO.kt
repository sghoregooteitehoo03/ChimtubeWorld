package com.sghore.chimtubeworld.retrofit.dto.youtubeAPI

data class PlaylistItemsDTO(
    val nextPageToken: String,
    val items: List<PlaylistItemDTO>
)