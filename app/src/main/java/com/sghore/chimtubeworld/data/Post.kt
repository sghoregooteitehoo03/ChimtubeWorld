package com.sghore.chimtubeworld.data

data class Post(
    val id: Int,
    val title: String,
    val userName: String,
    val postDate: String,
    val postImage: String,
    val url: String?,
    var isRead: Boolean = false
)
