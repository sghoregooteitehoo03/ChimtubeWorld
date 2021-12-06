package com.sghore.chimtubeworld.retrofit.dto.youtubeAPI

data class SnippetDTO(
    val title: String,
    val description: String,
    val publishedAt: String,
    val thumbnails: ThumbnailDTO
)
