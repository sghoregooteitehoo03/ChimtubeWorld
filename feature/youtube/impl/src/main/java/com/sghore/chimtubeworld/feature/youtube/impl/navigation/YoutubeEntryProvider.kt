package com.sghore.chimtubeworld.feature.youtube.impl.navigation

import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.sghore.chimtubeworld.core.model.VideoSource
import com.sghore.chimtubeworld.core.navigation.Navigator
import com.sghore.chimtubeworld.feature.bookmark.api.navigation.BookmarkResultKey
import com.sghore.chimtubeworld.feature.bookmark.api.navigation.navigateToEditBookmark
import com.sghore.chimtubeworld.feature.youtube.api.navigation.PlaylistsKey
import com.sghore.chimtubeworld.feature.youtube.api.navigation.VideosKey
import com.sghore.chimtubeworld.feature.youtube.api.navigation.YoutubeKey
import com.sghore.chimtubeworld.feature.youtube.api.navigation.navigateToPlaylists
import com.sghore.chimtubeworld.feature.youtube.api.navigation.navigateToVideos
import com.sghore.chimtubeworld.feature.youtube.impl.playlist_screen.PlaylistRoute
import com.sghore.chimtubeworld.feature.youtube.impl.playlist_screen.PlaylistViewModel
import com.sghore.chimtubeworld.feature.youtube.impl.videos_screen.VideosRoute
import com.sghore.chimtubeworld.feature.youtube.impl.videos_screen.VideosViewModel
import com.sghore.chimtubeworld.feature.youtube.impl.youtube_screen.YoutubeRoute

fun EntryProviderScope<NavKey>.youtubeEntry(navigator: Navigator) {
    entry<YoutubeKey> {
        YoutubeRoute(onChannelClick = { channel ->
            navigator.navigateToPlaylists(
                channelName = channel.name,
                channelId = if (channel.type == 0) channel.id.split("|")[0] else null,
                playlistId = channel.playlistId,
                type = channel.type
            )
        })
    }

    entry<PlaylistsKey> { key ->
        val viewModel = hiltViewModel<PlaylistViewModel, PlaylistViewModel.Factory>(
            creationCallback = { it.create(key.channelId, key.playlistId, key.type) }
        )
        PlaylistRoute(
            viewModel = viewModel,
            title = key.channelName,
            onBack = { navigator.goBack() },
            onPlaylistClick = { playlist ->
                navigator.navigateToVideos(playlist.id, playlist.title, VideoSource.YOUTUBE)
            }
        )
    }

    entry<VideosKey> { key ->
        val viewModel = hiltViewModel<VideosViewModel, VideosViewModel.Factory>(
            creationCallback = { it.create(key.playlistId) }
        )
        // Only the visible Videos entry consumes the result, including return from a share.
        val isCurrentEntry = navigator.state.currentKey == key
        LaunchedEffect(isCurrentEntry, viewModel) {
            if (isCurrentEntry) {
                navigator.consumeResult(BookmarkResultKey)?.let(viewModel::changeVideoBookmarks)
            }
        }
        VideosRoute(
            viewModel = viewModel,
            title = key.playlistName,
            source = key.source,
            onBack = { navigator.goBack() },
            onBookmarkClick = { video, pos ->
                navigator.navigateToEditBookmark(video, pos, key.source)
            }
        )
    }
}
