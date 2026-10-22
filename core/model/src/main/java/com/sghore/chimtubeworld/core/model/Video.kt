package com.sghore.chimtubeworld.core.model

import kotlinx.serialization.Serializable

@Serializable
data class Video(
    val id: String,
    val channelName: String,
    val title: String,
    val thumbnail: String,
    val uploadTime: Long,
    val viewCount: Long,
    val duration: Long,
    val url: String,
    var bookmarks: List<Bookmark> = listOf(),
)
