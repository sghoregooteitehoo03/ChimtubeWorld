package com.sghore.chimtubeworld.data

data class Channel(
    val name: String,
    val explain: String,
    val url: String,
    val image: String,
    val thumbnailImage: String? = null,
    val type: Int
) {
}