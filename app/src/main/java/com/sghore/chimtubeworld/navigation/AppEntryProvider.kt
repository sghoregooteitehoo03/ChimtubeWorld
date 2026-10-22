package com.sghore.chimtubeworld.navigation

import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.sghore.chimtubeworld.core.navigation.Navigator
import com.sghore.chimtubeworld.presentation.bookmarkScreen.AddBookmarkRoute
import com.sghore.chimtubeworld.presentation.bookmarkScreen.BookmarkViewModel
import com.sghore.chimtubeworld.presentation.bookmarkScreen.EditBookmarkRoute
import com.sghore.chimtubeworld.presentation.cafeScreen.CafeRoute
import com.sghore.chimtubeworld.presentation.playlistScreen.PlaylistRoute
import com.sghore.chimtubeworld.presentation.playlistScreen.PlaylistViewModel
import com.sghore.chimtubeworld.presentation.twitchScreen.TwitchRoute
import com.sghore.chimtubeworld.presentation.videosScreen.VideosRoute
import com.sghore.chimtubeworld.presentation.videosScreen.VideosViewModel
import com.sghore.chimtubeworld.presentation.youtubeScreen.YoutubeRoute

// Entries for screens that have not yet moved into feature modules.
internal fun EntryProviderScope<NavKey>.appEntries(navigator: Navigator) {
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
    entry<ChzzkKey> { TwitchRoute() }
    entry<CafeKey> { CafeRoute() }
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
    entry<AddBookmarkKey> { key ->
        val viewModel = hiltViewModel<BookmarkViewModel, BookmarkViewModel.Factory>(
            creationCallback = { it.create(null, -1, null, key.url) }
        )
        AddBookmarkRoute(
            viewModel = viewModel,
            onBack = { navigator.goBack() },
            onBookmarkChanged = { bookmark ->
                navigator.setResult(BookmarkResultKey, bookmark)
                navigator.goBack()
            }
        )
    }
    entry<EditBookmarkKey> { key ->
        val viewModel = hiltViewModel<BookmarkViewModel, BookmarkViewModel.Factory>(
            creationCallback = { it.create(key.video, key.pos, key.source, null) }
        )
        EditBookmarkRoute(
            viewModel = viewModel,
            onBack = { navigator.goBack() },
            onBookmarkChanged = { bookmark ->
                navigator.setResult(BookmarkResultKey, bookmark)
                navigator.goBack()
            }
        )
    }
}
