package com.sghore.chimtubeworld.navigation

import androidx.navigation3.runtime.NavKey
import com.sghore.chimtubeworld.core.navigation.Navigator
import kotlinx.serialization.Serializable

@Serializable
data object YoutubeKey : NavKey

@Serializable
data class PlaylistsKey(
    val channelName: String,
    val channelId: String?,
    val playlistId: String,
    val type: Int
) : NavKey

@Serializable
data class VideosKey(
    val playlistId: String,
    val playlistName: String,
    val source: VideoSource
) : NavKey

@Serializable
enum class VideoSource { YOUTUBE, TWITCH }

fun Navigator.navigateToPlaylists(
    channelName: String,
    channelId: String?,
    playlistId: String,
    type: Int
) {
    navigate(PlaylistsKey(channelName, channelId, playlistId, type))
}

fun Navigator.navigateToVideos(playlistId: String, playlistName: String, source: VideoSource) {
    navigate(VideosKey(playlistId, playlistName, source))
}
