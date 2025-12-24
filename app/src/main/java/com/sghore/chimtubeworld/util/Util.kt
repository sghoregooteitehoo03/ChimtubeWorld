package com.sghore.chimtubeworld.util

fun getVideoId(url: String): String? {
    val regex = Regex(
        "(?:https?://)?(?:www\\.)?(?:youtube\\.com|youtu\\.be)/(?:watch\\?v=|embed/|v/|shorts/|.+\\?v=)?([\\w-]{11})"
    )
    val matchResult = regex.find(url)
    return matchResult?.groups?.get(1)?.value
}