package com.sghore.chimtubeworld.retrofit.dto.twitchAPI

data class VideosDTO(
    val data: List<VideosDataDTO>,
    val pagination: PaginationDTO
)
