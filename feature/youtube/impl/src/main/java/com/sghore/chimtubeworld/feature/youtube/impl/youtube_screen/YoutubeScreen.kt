package com.sghore.chimtubeworld.feature.youtube.impl.youtube_screen

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.sghore.chimtubeworld.core.designsystem.component.RowItemCollocate
import com.sghore.chimtubeworld.core.designsystem.component.TitleTextWithExplain
import com.sghore.chimtubeworld.core.designsystem.theme.ChimtubeWorldTheme
import com.sghore.chimtubeworld.core.model.Channel
import com.sghore.chimtubeworld.core.designsystem.component.ScreenScaffold
import com.sghore.chimtubeworld.core.designsystem.R as DesignSystemR

@Composable
fun YoutubeScreen(
    uiState: YoutubeScreenState,
    onChannelClick: (Channel) -> Unit
) {
    ScreenScaffold {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    trackColor = Color.Transparent,
                    strokeCap = StrokeCap.Square,
                    gapSize = 0.dp,
                    modifier = Modifier.align(Alignment.Center),
                    color = colorResource(id = DesignSystemR.color.item_color)
                )
            } else {
                Column(modifier = Modifier.padding(12.dp)) {
                    TitleTextWithExplain(
                        title = "Youtube",
                        explain = "침튜브 채널"
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    MainYoutubeChannelList(
                        mainChannelList = uiState.channels?.filter { it?.type == 0 } ?: emptyList(),
                        onClick = onChannelClick
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    TitleTextWithExplain(
                        title = "Sub Contents",
                        explain = "침착맨 외부 방송"
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    SubYoutubeChannelList(
                        subChannelList = uiState.channels?.filter { it?.type == 1 } ?: emptyList(),
                        onClick = onChannelClick
                    )
                }
            }
        }
    }
}

@Composable
fun MainYoutubeChannelList(
    mainChannelList: List<Channel?>,
    onClick: (Channel) -> Unit
) {
    mainChannelList.forEach { channel ->
        Column {
            MainYoutubeChannelItem(
                channel = channel,
                modifier = Modifier
                    .fillMaxWidth(),
                onClick = onClick
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun MainYoutubeChannelItem(
    channel: Channel?,
    onClick: (Channel) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(colorResource(id = DesignSystemR.color.gray_bright_night))
            .clickable { onClick(channel!!) }
            .padding(16.dp)
    ) {
        AsyncImage(
            model = channel?.image,
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
                color = colorResource(id = DesignSystemR.color.item_color),
                fontSize = 18.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = channel?.explains?.get(0) ?: "",
                color = colorResource(id = DesignSystemR.color.default_text_color),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
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
                    onClick = onClick,
                    modifier = modifier,
                )
            },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun SubYoutubeChannelItem(
    channel: Channel?,
    onClick: (Channel) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clickable {
                onClick(channel!!)
            }) {
        AsyncImage(
            model = channel?.image,
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
            color = colorResource(id = DesignSystemR.color.item_color),
            fontSize = 18.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = channel?.name ?: "",
            color = colorResource(id = DesignSystemR.color.default_text_color),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Preview(name = "Light")
@Preview(name = "Dark", uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
fun TitleTextWihtExplainPreview() {
    ChimtubeWorldTheme(darkTheme = isSystemInDarkTheme()) {
        TitleTextWithExplain(
            "Youtube",
            "침튜브 채널"
        )
    }
}

@Preview(name = "Light")
@Preview(name = "Dark", uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
fun MainYoutubeChannelItemPreview() {
    ChimtubeWorldTheme(darkTheme = isSystemInDarkTheme()) {
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

@Preview(name = "Light")
@Preview(name = "Dark", uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
fun SubyoutubeChannelItemPreview() {
    ChimtubeWorldTheme(darkTheme = isSystemInDarkTheme()) {
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

@Preview(name = "Screen Light", showBackground = true)
@Preview(name = "Screen Dark", showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun YoutubeScreenPreview() {
    ChimtubeWorldTheme(darkTheme = androidx.compose.foundation.isSystemInDarkTheme()) {
        YoutubeScreen(uiState = YoutubeScreenState(), onChannelClick = {})
    }
}
