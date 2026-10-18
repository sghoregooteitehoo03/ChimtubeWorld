package com.sghore.chimtubeworld.core.model

data class Bookmark(
    val id: Int? = null,
    val videoId: String,
    val title: String,
    val videoPosition: Long,
    val color: Int
) {
    override fun equals(other: Any?): Boolean {
        other as Bookmark?
        return other?.id == id
    }
}
