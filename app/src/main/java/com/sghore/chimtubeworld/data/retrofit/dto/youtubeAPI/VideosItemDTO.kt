package com.sghore.chimtubeworld.data.retrofit.dto.youtubeAPI

data class VideosItemDTO(
    val id: String,
    val snippet: VideosSnippetDTO,
    val contentDetails: VideosDetailsDTO,
    val statistics: VideosStatisticsDTO
)
