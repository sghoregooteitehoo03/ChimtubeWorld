package com.sghore.chimtubeworld.feature.youtube.impl.videos_screen

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.sghore.chimtubeworld.core.designsystem.component.ScreenScaffold
import com.sghore.chimtubeworld.core.designsystem.theme.ChimtubeWorldTheme
import com.sghore.chimtubeworld.core.model.Video
import com.sghore.chimtubeworld.feature.youtube.impl.R
import com.sghore.chimtubeworld.feature.youtube.impl.videos_screen.component.SelectBookmarkDialog
import com.sghore.chimtubeworld.feature.youtube.impl.videos_screen.component.VideoItem
import com.sghore.chimtubeworld.feature.youtube.impl.videos_screen.component.VideoShimmerItem
import com.valentinilk.shimmer.*

@Composable
internal fun VideosScreen(
    uiState: VideosUiState,
    onEvent: (VideosUiEvent) -> Unit
) {
    uiState.selectedVideo?.let { video ->
        SelectBookmarkDialog(
            bookmarkList = video.bookmarks,
            onPositionSelected = { onEvent(VideosUiEvent.PositionSelected(it)) },
            onDismissRequest = { onEvent(VideosUiEvent.DialogDismissed) }
        )
    }

    ScreenScaffold(title = uiState.title, onBack = { onEvent(VideosUiEvent.BackClicked) }, actions = {
        IconButton(onClick = { onEvent(VideosUiEvent.HelpClicked) }) {
            Icon(Icons.AutoMirrored.Filled.Help, contentDescription = stringResource(R.string.videos_help), tint = MaterialTheme.colorScheme.onPrimary)
        }
    }) {
        val videoList: LazyPagingItems<Video>? = uiState.videos?.collectAsLazyPagingItems()
        videoList?.retry()
        val isLoading by remember {
            derivedStateOf {
                videoList?.loadState?.refresh is LoadState.Loading
            }
        }

        LazyColumn {
            videoList?.let {
                if (!isLoading) {
                    items(count = it.itemCount) { index ->
                        VideoItem(
                            video = it[index],
                            onVideoClick = { onEvent(VideosUiEvent.VideoClicked(it)) },
                            onBookmarkClick = { video, position -> onEvent(VideosUiEvent.BookmarkClicked(video, position)) }
                        )
                    }
                } else {
                    items(3) {
                        VideoShimmerItem()
                    }
                }
            }
        }
    }
}

@Preview(name = "Screen Light", showBackground = true)
@Preview(name = "Screen Dark", showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun VideosScreenPreview() {
    ChimtubeWorldTheme(darkTheme = isSystemInDarkTheme()) {
        VideosScreen(VideosUiState(title = "재생목록"), {})
    }
}
