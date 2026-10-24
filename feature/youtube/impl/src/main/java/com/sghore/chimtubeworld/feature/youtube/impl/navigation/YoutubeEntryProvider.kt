package com.sghore.chimtubeworld.feature.youtube.impl.navigation

import android.widget.Toast
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.sghore.chimtubeworld.core.model.VideoSource
import com.sghore.chimtubeworld.core.navigation.Navigator
import com.sghore.chimtubeworld.core.ui.OpenOtherApp
import com.sghore.chimtubeworld.feature.bookmark.api.navigation.BookmarkResultKey
import com.sghore.chimtubeworld.feature.bookmark.api.navigation.navigateToEditBookmark
import com.sghore.chimtubeworld.feature.youtube.api.navigation.PlaylistsKey
import com.sghore.chimtubeworld.feature.youtube.api.navigation.VideosKey
import com.sghore.chimtubeworld.feature.youtube.api.navigation.YoutubeKey
import com.sghore.chimtubeworld.feature.youtube.api.navigation.navigateToPlaylists
import com.sghore.chimtubeworld.feature.youtube.api.navigation.navigateToVideos
import com.sghore.chimtubeworld.feature.youtube.impl.R
import com.sghore.chimtubeworld.feature.youtube.impl.playlist_screen.PlaylistRoute
import com.sghore.chimtubeworld.feature.youtube.impl.playlist_screen.PlaylistUiSideEffect
import com.sghore.chimtubeworld.feature.youtube.impl.playlist_screen.PlaylistViewModel
import com.sghore.chimtubeworld.feature.youtube.impl.videos_screen.VideosRoute
import com.sghore.chimtubeworld.feature.youtube.impl.videos_screen.VideosUiEvent
import com.sghore.chimtubeworld.feature.youtube.impl.videos_screen.VideosUiSideEffect
import com.sghore.chimtubeworld.feature.youtube.impl.videos_screen.VideosViewModel
import com.sghore.chimtubeworld.feature.youtube.impl.youtube_screen.YoutubeRoute
import com.sghore.chimtubeworld.feature.youtube.impl.youtube_screen.YoutubeUiSideEffect

fun EntryProviderScope<NavKey>.youtubeEntry(navigator: Navigator) {
    entry<YoutubeKey> {
        YoutubeRoute(onSideEffect = { sideEffect ->
            when (sideEffect) {
                is YoutubeUiSideEffect.NavigateToPlaylists -> {
                    val channel = sideEffect.channel
                    navigator.navigateToPlaylists(
                        channelName = channel.name,
                        channelId = if (channel.type == 0) channel.id.split("|")[0] else null,
                        playlistId = channel.playlistId,
                        type = channel.type
                    )
                }
            }
        })
    }

    entry<PlaylistsKey> { key ->
        val viewModel = hiltViewModel<PlaylistViewModel, PlaylistViewModel.Factory>(
            creationCallback = { it.create(key.channelId, key.playlistId, key.type, key.channelName) }
        )
        PlaylistRoute(viewModel = viewModel, onSideEffect = { sideEffect ->
            when (sideEffect) {
                PlaylistUiSideEffect.NavigateBack -> navigator.goBack()
                is PlaylistUiSideEffect.NavigateToVideos -> navigator.navigateToVideos(
                    sideEffect.playlist.id, sideEffect.playlist.title, VideoSource.YOUTUBE
                )
            }
        })
    }

    entry<VideosKey> { key ->
        val context = LocalContext.current
        val viewModel = hiltViewModel<VideosViewModel, VideosViewModel.Factory>(
            creationCallback = { it.create(key.playlistId, key.playlistName, key.source) }
        )
        // Only the visible Videos entry consumes the result, including return from a share.
        val isCurrentEntry = navigator.state.currentKey == key
        LaunchedEffect(isCurrentEntry, viewModel) {
            if (isCurrentEntry) {
                navigator.consumeResult(BookmarkResultKey)?.let {
                    viewModel.onEvent(VideosUiEvent.BookmarkResultReceived(it))
                }
            }
        }
        VideosRoute(viewModel = viewModel, onSideEffect = { sideEffect ->
            when (sideEffect) {
                VideosUiSideEffect.NavigateBack -> navigator.goBack()
                VideosUiSideEffect.ShowHelp -> Toast.makeText(
                    context, context.getString(R.string.videos_help_message), Toast.LENGTH_SHORT
                ).show()
                is VideosUiSideEffect.NavigateToEditBookmark -> navigator.navigateToEditBookmark(
                    sideEffect.video, sideEffect.position, sideEffect.source
                )
                is VideosUiSideEffect.OpenVideo -> {
                    if (sideEffect.source == VideoSource.YOUTUBE) {
                        OpenOtherApp(context).openYoutube(sideEffect.packageName, sideEffect.url)
                    } else {
                        OpenOtherApp(context).openTwitch(sideEffect.packageName, sideEffect.url)
                    }
                }
            }
        })
    }
}
