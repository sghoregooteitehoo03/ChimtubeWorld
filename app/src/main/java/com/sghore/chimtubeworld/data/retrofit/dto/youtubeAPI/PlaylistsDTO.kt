package com.sghore.chimtubeworld.data.retrofit.dto.youtubeAPI

data class PlaylistsDTO(
    val nextPageToken: String? = null,
    val items: List<PlaylistsItemsDTO>
)
