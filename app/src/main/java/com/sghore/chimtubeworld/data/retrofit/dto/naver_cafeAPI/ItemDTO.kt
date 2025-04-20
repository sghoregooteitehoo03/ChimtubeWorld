package com.sghore.chimtubeworld.data.retrofit.dto.naver_cafeAPI

data class ItemDTO(
    val articleId: Int,
    val writerInfo: WriterInfoDTO,
    val menuId: Int,
    val headName: String? = null,
    val subject: String,
    val representImage: String? = null,
    val writeDateTimestamp: Long
)