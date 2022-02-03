package com.sghore.chimtubeworld.presentation.youtubeScreen

import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import com.sghore.chimtubeworld.R
import com.sghore.chimtubeworld.data.model.Channel
import com.sghore.chimtubeworld.presentation.RowItemCollocate
import com.sghore.chimtubeworld.presentation.TitleTextWithExplain

@Composable
fun YoutubeScreen(
    uiState: YoutubeScreenState,
    onClick: (Channel) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            if (!uiState.isLoading) {
                TitleTextWithExplain(
                    title = "Youtube",
                    explain = "침튜브 채널"
                )
                Spacer(modifier = Modifier.height(16.dp))
                MainYoutubeChannelList(
                    mainChannelList = uiState.channels?.filter { it?.type == 0 }
                        ?: listOf(),
                    onClick = onClick
                )

                Spacer(modifier = Modifier.height(8.dp))

                TitleTextWithExplain(
                    title = "Sub Contents",
                    explain = "침착맨 외부 방송"
                )
                Spacer(modifier = Modifier.height(16.dp))
                SubYoutubeChannelList(
                    subChannelList = uiState.channels?.filter { it?.type == 1 } ?: listOf(),
                    onClick = onClick
                )
            }
        }

        if (uiState.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = colorResource(id = R.color.item_color)
            )
        }
    }
}

@Composable
fun MainYoutubeChannelList(
    mainChannelList: List<Channel?>,
    onClick: (Channel) -> Unit = {}
) {
    mainChannelList.forEach { channel ->
        Column {
            MainYoutubeChannelItem(
                channel = channel,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                onClick = onClick
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun MainYoutubeChannelItem(
    channel: Channel?,
    modifier: Modifier = Modifier,
    onClick: (Channel) -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(colorResource(id = R.color.gray_bright_night))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(
                    bounded = true,
                    color = colorResource(id = R.color.item_color)
                )
            ) {
                onClick(channel!!)
            }
    ) {
        Row(
            modifier = modifier
        ) {
            Image(
                painter = rememberImagePainter(
                    data = channel?.image
                ),
                contentDescription = channel?.id,
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(
                modifier = Modifier.align(Alignment.CenterVertically)
            ) {
                Text(
                    text = channel?.name ?: "",
                    color = colorResource(id = R.color.item_color),
                    fontSize = 18.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = channel?.explains?.get(0) ?: "",
                    color = colorResource(id = R.color.default_text_color),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun SubYoutubeChannelList(
    subChannelList: List<Channel?>,
    onClick: (Channel) -> Unit = {}
) {
    val spanCount = 2
    val itemCount = if (subChannelList.size % spanCount == 0) {
        subChannelList.size / spanCount
    } else {
        subChannelList.size / spanCount + 1
    }

    for (rowIndex in 0 until itemCount) {
        RowItemCollocate(
            rowIndex = rowIndex,
            list = subChannelList,
            spanCount = spanCount,
            paddingValue = 12.dp,
            content = { index, modifier ->
                SubYoutubeChannelItem(
                    channel = subChannelList[index],
                    modifier = modifier,
                    onClick = onClick
                )
            },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun SubYoutubeChannelItem(
    channel: Channel?,
    modifier: Modifier = Modifier,
    onClick: (Channel) -> Unit = {}
) {
    Box(
        modifier = modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(
                    bounded = true,
                    color = colorResource(id = R.color.item_color)
                )
            ) {
                onClick(channel!!)
            }
    ) {
        Column {
            Image(
                painter = rememberImagePainter(
                    data = channel?.image
                ),
                contentDescription = channel?.id,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = channel?.explains?.get(0) ?: "",
                color = colorResource(id = R.color.item_color),
                fontSize = 18.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = channel?.name ?: "",
                color = colorResource(id = R.color.default_text_color),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Preview
@Composable
fun TitleTextWihtExplainPreview() {
    MaterialTheme {
        TitleTextWithExplain(
            "Youtube",
            "침튜브 채널"
        )
    }
}

@Preview
@Composable
fun MainYoutubeChannelItemPreview() {
    MaterialTheme {
        MainYoutubeChannelItem(
            channel = Channel(
                id = "",
                name = "침착맨",
                explains = arrayOf("좋은 하루입니다."),
                url = "",
                image = "https://yt3.ggpht.com/ytc/AKedOLQPSkWqeI1LiBS9_gvdvA2QhshcDpYYkCtLtIskFg=s176-c-k-c0x00ffffff-no-rj",
                type = 0
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            onClick = {

            }
        )
    }
}

@Preview
@Composable
fun SubyoutubeChannelItemPreview() {
    MaterialTheme {
        SubYoutubeChannelItem(
            channel = Channel(
                id = "",
                name = "엠드로메다",
                explains = arrayOf("말년을 건강하게"),
                url = "",
                image = "https://yt3.ggpht.com/ytc/AKedOLQPSkWqeI1LiBS9_gvdvA2QhshcDpYYkCtLtIskFg=s176-c-k-c0x00ffffff-no-rj",
                type = 0
            ),
            onClick = {

            }
        )
    }
}