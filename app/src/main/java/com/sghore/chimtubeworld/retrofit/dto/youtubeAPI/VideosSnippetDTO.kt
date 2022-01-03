package com.sghore.chimtubeworld.retrofit.dto.youtubeAPI

data class VideosSnippetDTO(
    val publishedAt: String,
    val title: String,
    val thumbnails: ThumbnailDTO,
    val channelTitle: String
)