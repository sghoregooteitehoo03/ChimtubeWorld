package com.sghore.chimtubeworld.feature.youtube.impl.playlist_screen

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.sghore.chimtubeworld.core.designsystem.R as DesignSystemR
import com.sghore.chimtubeworld.core.designsystem.component.ScreenScaffold
import com.sghore.chimtubeworld.core.designsystem.theme.ChimtubeWorldTheme
import com.sghore.chimtubeworld.feature.youtube.impl.playlist_screen.component.PlaylistItem
import com.sghore.chimtubeworld.feature.youtube.impl.playlist_screen.component.PlaylistItemShimmer

@Composable
internal fun PlaylistScreen(
    uiState: PlaylistUiState,
    onEvent: (PlaylistUiEvent) -> Unit
) {
    ScreenScaffold(title = uiState.title, onBack = { onEvent(PlaylistUiEvent.BackClicked) }) {
        val playlists = uiState.playlists?.collectAsLazyPagingItems()
        val isLoading by remember {
            derivedStateOf {
                playlists?.loadState?.refresh is LoadState.Loading
            }
        }

        LazyColumn(contentPadding = PaddingValues(top = 12.dp, start = 12.dp, end = 12.dp)) {
            playlists?.let {
                if (!isLoading) {
                    items(count = it.itemCount) { index ->
                        PlaylistItem(
                            playlist = it[index],
                            onClick = { onEvent(PlaylistUiEvent.PlaylistClicked(it)) },
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                    }
                } else {
                    items(3) {
                        PlaylistItemShimmer(
                            modifier = Modifier
                                .padding(bottom = 12.dp)
                                .background(
                                    color = colorResource(id = DesignSystemR.color.gray_night),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .clip(RoundedCornerShape(8.dp))
                        )
                    }
                }
            }
        }
    }
}

@Preview(name = "Screen Light", showBackground = true)
@Preview(name = "Screen Dark", showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PlaylistScreenPreview() {
    ChimtubeWorldTheme(darkTheme = isSystemInDarkTheme()) {
        PlaylistScreen(PlaylistUiState(title = "침착맨"), {})
    }
}
