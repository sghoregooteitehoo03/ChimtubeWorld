package com.sghore.chimtubeworld.presentation.youtubeScreen

import android.util.Log
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import coil.request.ImageRequest
import com.google.accompanist.flowlayout.FlowColumn
import com.google.accompanist.flowlayout.FlowMainAxisAlignment
import com.google.accompanist.flowlayout.FlowRow
import com.google.accompanist.flowlayout.SizeMode
import com.sghore.chimtubeworld.R
import com.sghore.chimtubeworld.data.model.Channel

@Composable
fun YoutubeScreen(
    viewModel: YoutubeViewModel,
    onClick: (Channel?) -> Unit
) {
    val state = viewModel.state.value
    val scrollState = rememberScrollState()

    val mainChannelList = state.channels?.filter { it?.type == 0 } ?: listOf()
    val subChannelList = state.channels?.filter { it?.type == 1 } ?: listOf()

    val itemCount = if (subChannelList.size % 2 == 0) {
        subChannelList.size / 2
    } else {
        subChannelList.size / 2 + 1
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(scrollState)
    ) {
        if (state.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .align(Alignment.Center),
                color = colorResource(id = R.color.item_color)
            )
        } else {
            Column {
                TitleTextWithExplain(
                    title = "Youtube",
                    explain = "침튜브 채널",
                    modifier = Modifier.padding(top = 12.dp, start = 12.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))

                mainChannelList.forEach { channel ->
                    Column(modifier = Modifier.padding(start = 12.dp, end = 12.dp)) {
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

                Spacer(modifier = Modifier.height(8.dp))
                TitleTextWithExplain(
                    title = "Sub Contents",
                    explain = "침착맨 외부 방송",
                    modifier = Modifier.padding(start = 12.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))

                for (index in 0 until itemCount) {
                    SubYoutubeChannelRow(
                        rowIndex = index,
                        channels = subChannelList,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 12.dp, end = 12.dp),
                        onClick = onClick
                    )
                }
            }
//            LazyColumn {
//                item {
//                    TitleTextWithExplain(
//                        title = "Youtube",
//                        explain = "침튜브 채널",
//                        modifier = Modifier.padding(top = 12.dp, start = 12.dp)
//                    )
//                    Spacer(modifier = Modifier.height(16.dp))
//                }
//                items(mainChannelList) { channel ->
//                    Column(modifier = Modifier.padding(start = 12.dp, end = 12.dp)) {
//                        MainYoutubeChannelItem(
//                            channel = channel,
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .padding(16.dp),
//                            onClick = onClick
//                        )
//                        Spacer(modifier = Modifier.height(16.dp))
//                    }
//                }
//                item {
//                    Spacer(modifier = Modifier.height(8.dp))
//                    TitleTextWithExplain(
//                        title = "Sub Contents",
//                        explain = "침착맨 외부 방송",
//                        modifier = Modifier.padding(start = 12.dp)
//                    )
//                    Spacer(modifier = Modifier.height(16.dp))
//                }
//                items(itemCount) { index ->
//                    SubYoutubeChannelRow(
//                        rowIndex = index,
//                        channels = subChannelList,
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .padding(start = 12.dp, end = 12.dp),
//                        onClick = onClick
//                    )
//                }
//            }
        }
    }
}

@Composable
fun TitleTextWithExplain(
    title: String,
    explain: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.Bottom
    ) {
        Text(
            text = title,
            color = colorResource(id = R.color.item_color),
            fontSize = 22.sp
        )
        Spacer(Modifier.width(6.dp))
        Text(
            text = explain,
            color = colorResource(id = R.color.default_text_color),
            fontSize = 16.sp
        )
    }
}

@Composable
fun MainYoutubeChannelItem(
    channel: Channel?,
    modifier: Modifier = Modifier,
    onClick: (Channel?) -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(colorResource(id = R.color.gray_bright_night))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(bounded = true)
            ) {
                onClick(channel)
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
fun SubYoutubeChannelRow(
    rowIndex: Int,
    channels: List<Channel?>,
    modifier: Modifier = Modifier,
    onClick: (Channel?) -> Unit
) {
    Column(modifier = modifier) {
        Row {
            SubYoutubeChannelItem(
                channel = channels[rowIndex * 2],
                modifier = Modifier.weight(1f),
                onClick = onClick
            )
            Spacer(modifier = Modifier.width(12.dp))
            if (channels.size >= rowIndex * 2 + 2) {
                SubYoutubeChannelItem(
                    channel = channels[rowIndex * 2 + 1],
                    modifier = Modifier.weight(1f),
                    onClick = onClick
                )
            } else {
                Spacer(modifier = Modifier.weight(1f))
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
    }
}

@Composable
fun SubYoutubeChannelItem(
    channel: Channel?,
    modifier: Modifier = Modifier,
    onClick: (Channel?) -> Unit
) {
    Box(
        modifier = modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(bounded = true)
            ) {
                onClick(channel)
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