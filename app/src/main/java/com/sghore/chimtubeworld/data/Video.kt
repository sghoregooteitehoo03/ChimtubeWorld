package com.sghore.chimtubeworld.data

import java.time.Duration

data class Video(
    val id: String,
    val title: String,
    val thumbnail: String,
    val uploadTime: Long,
    val viewCount: Long,
    val duration: Long,
    val url: String
) {
}