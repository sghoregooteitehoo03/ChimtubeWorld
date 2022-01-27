package com.sghore.chimtubeworld.data.model

data class Channel(
    val id: String,
    val name: String,
    val explains: Array<String>,
    val url: String,
    val image: String,
    val thumbnailImage: String? = null,
    val type: Int,
    val isOnline: Boolean? = null
) {
    var isSelected: Boolean = false
}