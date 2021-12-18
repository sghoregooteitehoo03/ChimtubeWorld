package com.sghore.chimtubeworld.retrofit.dto.youtubeAPI

data class ChannelSnippetDTO(
    val title: String,
    val description: String,
    val publishedAt: String,
    val thumbnails: ThumbnailDTO
)
