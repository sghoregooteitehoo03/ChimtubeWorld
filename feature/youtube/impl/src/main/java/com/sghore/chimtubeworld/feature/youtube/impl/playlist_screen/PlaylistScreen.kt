package com.sghore.chimtubeworld.feature.youtube.impl.playlist_screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.PlaylistPlay
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.sghore.chimtubeworld.core.designsystem.theme.ChimtubeWorldTheme
import com.sghore.chimtubeworld.core.model.Playlist
import com.sghore.chimtubeworld.core.designsystem.component.ScreenScaffold
import com.valentinilk.shimmer.shimmer
import com.sghore.chimtubeworld.core.designsystem.R as DesignSystemR

@Composable
fun PlaylistScreen(
    title: String = "",
    onBack: () -> Unit = {},
    uiState: PlaylistScreenState,
    onPlaylistClick: (Playlist) -> Unit
) {
    ScreenScaffold(title = title, onBack = onBack) {
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
                            onClick = onPlaylistClick,
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

@Composable
fun PlaylistItem(
    playlist: Playlist?,
    onClick: (Playlist) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick(playlist!!) }
    ) {
        AsyncImage(
            model = playlist?.thumbnailImage,
            contentDescription = "playlistImage",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .aspectRatio(16 / 9f)
                .background(
                    colorResource(id = DesignSystemR.color.gray_night),
                    shape = RoundedCornerShape(8.dp)
                )
                .clip(RoundedCornerShape(8.dp))
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clip(
                    RoundedCornerShape(
                        bottomStart = 8.dp,
                        bottomEnd = 8.dp
                    )
                )
                .background(color = Color.Black.copy(alpha = 0.6f))
                .align(Alignment.BottomStart)
                .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = playlist?.title ?: "",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(18.dp))
            Icon(
                imageVector = Icons.AutoMirrored.Filled.PlaylistPlay,
                contentDescription = "Playlist",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun PlaylistItemShimmer(
    modifier: Modifier
) {
    Box(
        modifier = modifier
            .aspectRatio(16 / 9f)
            .shimmer()
    )
}

@Preview(name = "Light")
@Preview(name = "Dark", uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PlaylistItemPreview() {
    ChimtubeWorldTheme(darkTheme = isSystemInDarkTheme()) {
        val playlist = Playlist(
            id = "",
            title = "\uD83D\uDCAA\uD83C\uDFFB말년을 건강하게\uD83D\uDCAA\uD83C\uDFFB│침착맨의 노후대비 운동입문기",
            thumbnailImage = ""
        )
        PlaylistItem(
            playlist = playlist,
            onClick = {}
        )
    }
}

@Preview(name = "Screen Light", showBackground = true)
@Preview(name = "Screen Dark", showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PlaylistScreenPreview() {
    ChimtubeWorldTheme(darkTheme = androidx.compose.foundation.isSystemInDarkTheme()) {
        PlaylistScreen(title = "침착맨", uiState = PlaylistScreenState(), onPlaylistClick = {})
    }
}
