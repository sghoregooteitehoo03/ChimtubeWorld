package com.sghore.chimtubeworld.data.retrofit.dto.twitchAPI

data class VideosDTO(
    val data: List<VideosDataDTO>,
    val pagination: PaginationDTO
)
