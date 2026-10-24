package com.sghore.chimtubeworld.core.network.dto.youtubeAPI

data class VideosSnippetDTO(
    val publishedAt: String,
    val title: String,
    val thumbnails: ThumbnailDTO,
    val channelTitle: String
)