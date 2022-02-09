package com.sghore.chimtubeworld.data.model

import android.os.Parcelable
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Parcelize
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
) : Parcelable {
    @IgnoredOnParcel
    var currentPageKey: String? = null
}