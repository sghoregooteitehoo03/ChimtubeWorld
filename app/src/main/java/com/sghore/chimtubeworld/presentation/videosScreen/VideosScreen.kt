package com.sghore.chimtubeworld.presentation.videosScreen

import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import coil.compose.rememberImagePainter
import com.sghore.chimtubeworld.R
import com.sghore.chimtubeworld.data.model.Bookmark
import com.sghore.chimtubeworld.data.model.Video
import com.sghore.chimtubeworld.util.parseUploadTimeText
import com.sghore.chimtubeworld.util.parseVideoDurationText
import com.sghore.chimtubeworld.util.parseViewCountText
import com.valentinilk.shimmer.*

@Composable
fun VideosScreen(
    uiState: VideosScreenState,
    onVideoClick: (Video) -> Unit,
    onBookmarkClick: (Video, Int) -> Unit,
) {
    val videoList: LazyPagingItems<Video>? = uiState.videos?.collectAsLazyPagingItems()
    val isLoading by remember {
        derivedStateOf {
            videoList?.loadState?.refresh is LoadState.Loading
        }
    }

    LazyColumn {
        videoList?.let {
            if (!isLoading) {
                items(items = it) { video ->
                    VideoItem(
                        video = video,
                        onVideoClick = onVideoClick,
                        onBookmarkClick = onBookmarkClick
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

@Composable
fun VideoItem(
    video: Video?,
    onVideoClick: (Video) -> Unit = {},
    onBookmarkClick: (Video, Int) -> Unit = { videoData, bookmarkPos -> }
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(
                    color = colorResource(id = R.color.item_color)
                )
            ) {
                onVideoClick(video!!)
            }
    )
    {
        Box {
            Image(
                painter = rememberImagePainter(data = video?.thumbnail),
                contentDescription = video?.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.aspectRatio(16 / 9f)
            )
            Column(
                modifier = Modifier
                    .padding(8.dp)
                    .align(Alignment.BottomEnd)
            ) {
                Text(
                    text = parseVideoDurationText(video?.duration ?: -32400000),
                    color = Color.White,
                    fontSize = 12.sp,
                    modifier = Modifier
                        .background(
                            color = colorResource(id = R.color.black_alpha_80),
                            shape = RoundedCornerShape(4.dp)
                        )
                        .padding(start = 6.dp, end = 6.dp, top = 2.dp, bottom = 2.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = video?.title ?: "",
            color = colorResource(id = R.color.item_color),
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 12.dp, end = 12.dp),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = "${parseViewCountText(video?.viewCount ?: 0)} • ${parseUploadTimeText(video?.uploadTime ?: 0)}",
            color = colorResource(id = R.color.default_text_color),
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 12.dp, end = 12.dp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(modifier = Modifier.height(12.dp))

        if (video?.bookmarks?.isNotEmpty() == true) {
            LazyRow {
                item {
                    Spacer(modifier = Modifier.width(12.dp))
                }

                items(video.bookmarks.size) { index ->
                    BookmarkItem(
                        bookmark = video.bookmarks[index],
                        onClick = {
                            onBookmarkClick(video, index)
                        }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
fun VideoShimmerItem() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .shimmer()
    ) {
        Box(
            modifier = Modifier
                .aspectRatio(16 / 9f)
                .background(color = colorResource(id = R.color.gray_night))
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "",
            modifier = Modifier
                .padding(start = 12.dp, end = 12.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(color = colorResource(id = R.color.gray_night))
                .fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "",
            modifier = Modifier
                .padding(start = 12.dp, end = 12.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(color = colorResource(id = R.color.gray_night))
                .fillMaxWidth(0.4f)
        )
        Spacer(modifier = Modifier.height(12.dp))
    }
}

@Composable
fun BookmarkItem(
    bookmark: Bookmark,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(
                color = colorResource(id = R.color.gray_bright_night)
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(
                    color = colorResource(id = R.color.item_color)
                )
            ) {
                onClick()
            }
            .padding(top = 6.dp, bottom = 6.dp, start = 4.dp, end = 4.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(18.dp)
                .clip(CircleShape)
                .background(color = Color(bookmark.color))
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = bookmark.title,
            color = colorResource(id = R.color.item_color),
            fontSize = 12.sp
        )
    }
}