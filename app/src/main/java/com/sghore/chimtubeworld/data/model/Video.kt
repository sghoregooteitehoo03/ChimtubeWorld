package com.sghore.chimtubeworld.data.model

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