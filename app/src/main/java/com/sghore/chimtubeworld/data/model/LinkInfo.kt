package com.sghore.chimtubeworld.data.model

data class LinkInfo(
    val id: String,
    val url: String,
    val explain: String,
    val type: Int = -1,
    val playlistId: String = "",
    val playlistName: String = ""
)
