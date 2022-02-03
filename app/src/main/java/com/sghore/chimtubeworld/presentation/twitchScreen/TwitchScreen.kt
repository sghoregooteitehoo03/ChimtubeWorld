package com.sghore.chimtubeworld.presentation.twitchScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import com.sghore.chimtubeworld.R
import com.sghore.chimtubeworld.data.model.Channel
import com.sghore.chimtubeworld.presentation.RowList
import com.sghore.chimtubeworld.presentation.TitleTextWithExplain
import com.sghore.chimtubeworld.util.parseFollowText

@Composable
fun TwitchScreen(
    uiState: TwitchScreenState,
    onMainChannelClick: (Channel?) -> Unit,
    onTwitchCrewChannelClick: (Channel) -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        if (uiState.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = colorResource(id = R.color.item_color)
            )
        } else {
            val crewChannelList = uiState.channels ?: emptyList()
            RowList(
                list = crewChannelList,
                spanCount = 4,
                contentPaddingValue = 12.dp,
                itemPaddingValue = 12.dp,
                headerItem = {
                    TitleTextWithExplain(
                        title = "Twitch Live",
                        explain = "침착맨 생방송 채널"
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    MainChannelInfo(
                        mainChannel = uiState.mainChannelInfo,
                        onClick = onMainChannelClick
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    TitleTextWithExplain(
                        title = "Twitch Crew",
                        explain = "스트리머 크루 배도라지"
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                },
                listItem = { index, modifier ->
                    TwitchCrewChannelItem(
                        channel = crewChannelList[index],
                        modifier = modifier,
                        onClick = onTwitchCrewChannelClick
                    )
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun MainChannelInfo(
    mainChannel: Channel?,
    onClick: (Channel?) -> Unit = { }
) {
    val colorFilter = if (mainChannel?.isOnline == true) {
        null
    } else {
        ColorFilter.tint(
            colorResource(id = R.color.black_alpha_50),
            blendMode = BlendMode.SrcOver
        )
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(
                    bounded = true,
                    color = colorResource(id = R.color.item_color)
                )
            ) {
                onClick(mainChannel)
            }
    ) {
        Image(
            painter = rememberImagePainter(
                data = mainChannel?.thumbnailImage
            ),
            contentDescription = mainChannel?.id,
            modifier = Modifier
                .fillMaxWidth()
                .height(208.dp)
                .clip(RoundedCornerShape(12.dp)),
            contentScale = ContentScale.Crop,
            colorFilter = colorFilter
        )
        Spacer(modifier = Modifier.height(12.dp))
        Row {
            Box {
                Image(
                    painter = rememberImagePainter(
                        data = mainChannel?.image
                    ),
                    contentDescription = mainChannel?.id,
                    modifier = Modifier
                        .size(64.dp)
                        .border(
                            width = 2.dp,
                            color = if (mainChannel?.isOnline == true) colorResource(
                                id = R.color.green_online_color
                            ) else colorResource(
                                id = android.R.color.darker_gray
                            ),
                            shape = CircleShape
                        )
                        .padding(4.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                Column(
                    modifier = Modifier
                        .align(Alignment.Center)
                ) {
                    Spacer(modifier = Modifier.height(50.dp))
                    if (mainChannel?.isOnline == true) {
                        Text(
                            text = "생방송",
                            color = colorResource(id = R.color.white),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .border(
                                    width = 2.dp,
                                    color = colorResource(id = R.color.item_reverse_color),
                                    shape = RoundedCornerShape(4.dp)
                                )
                                .background(
                                    color = colorResource(id = R.color.red_online_color),
                                    shape = RoundedCornerShape(4.dp)
                                )
                                .padding(start = 4.dp, end = 4.dp, top = 2.dp, bottom = 2.dp)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(
                modifier = Modifier.height(60.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Row {
                    Text(
                        text = mainChannel?.name ?: "",
                        color = colorResource(id = R.color.item_color),
                        fontSize = 18.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Image(
                        imageVector = ImageVector.vectorResource(id = R.drawable.ic_twitch_bedge),
                        contentDescription = "TwitchBedge"
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = parseFollowText(mainChannel?.explains?.get(1)),
                    color = colorResource(id = R.color.default_text_color),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun TwitchCrewChannelItem(
    channel: Channel?,
    modifier: Modifier = Modifier,
    onClick: (Channel) -> Unit = { }
) {
    Box(
        modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(
                    bounded = true,
                    color = colorResource(id = R.color.item_color)
                )
            ) {
                onClick(channel!!)
            },
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = rememberImagePainter(
                    data = channel?.image
                ),
                contentDescription = channel?.id,
                modifier = Modifier
                    .size(80.dp)
                    .aspectRatio(1f)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = channel?.name ?: "",
                color = colorResource(id = R.color.item_color),
                fontSize = 18.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = channel?.explains?.get(0) ?: "",
                color = colorResource(id = R.color.default_text_color),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Preview
@Composable
fun MainChannelInfoPreview() {
    MaterialTheme {
        MainChannelInfo(
            mainChannel = Channel(
                "",
                "침착맨",
                arrayOf("", "590000"),
                "",
                "",
                type = 0,
                isOnline = false
            )
        )
    }
}

@Preview
@Composable
fun TwitchCrewChannelItemPreview() {
    MaterialTheme {
        TwitchCrewChannelItem(
            channel = Channel(
                "",
                "침착맨",
                arrayOf("준회원", "590000"),
                "",
                "",
                type = 0
            )
        )
    }
}
