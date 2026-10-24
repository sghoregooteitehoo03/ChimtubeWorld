package com.sghore.chimtubeworld.feature.youtube.impl.playlist_screen.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.PlaylistPlay
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.sghore.chimtubeworld.core.designsystem.R as DesignSystemR
import com.sghore.chimtubeworld.core.designsystem.theme.ChimtubeWorldTheme
import com.sghore.chimtubeworld.core.model.Playlist
import com.sghore.chimtubeworld.feature.youtube.impl.R
import com.valentinilk.shimmer.shimmer

@Composable
internal fun PlaylistItem(
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
            contentDescription = stringResource(R.string.playlist_image),
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
                contentDescription = stringResource(R.string.playlist_icon),
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
internal fun PlaylistItemShimmer(
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
private fun PlaylistItemPreview() {
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

@Preview(showBackground = true)
@Composable
private fun PlaylistShimmerPreview() {
    ChimtubeWorldTheme { PlaylistItemShimmer(Modifier.fillMaxWidth()) }
}
