package com.sghore.chimtubeworld.data

data class Channel(
    val name: String,
    val explains: Array<String>,
    val url: String,
    val image: String,
    val thumbnailImage: String? = null,
    val type: Int,
    val isOnline: Boolean? = null
) {
}