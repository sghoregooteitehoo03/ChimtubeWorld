package com.sghore.chimtubeworld.presentation.twitchScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.sghore.chimtubeworld.R
import com.sghore.chimtubeworld.other.Contents
import com.sghore.chimtubeworld.other.OpenOtherApp

@Composable
fun TwitchRoute(viewModel: TwitchViewModel = hiltViewModel()) {
    val uiState by viewModel.state.collectAsState()
    val context = LocalContext.current

    TwitchScreen(
        uiState = uiState,
        onMainChannelClick = { viewModel.setDialog(true) },
        onTwitchCrewChannelClick = { channel ->
            // 트위치 채널로 이동
            OpenOtherApp(context).openTwitch(
                packageName = Contents.TWITCH_CHANNEL_PACKAGE_NAME + channel.name,
                url = channel.url
            )
        }
    )

    if (uiState.isDialogOpen) {
        val channel = uiState.mainChannelInfo
        Dialog(onDismissRequest = { viewModel.setDialog(false) }) {
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(color = colorResource(id = R.color.default_background_color))
                    .padding(16.dp)
            ) {
                SelectionItem(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(color = colorResource(id = R.color.gray_bright_night)),
                    image = painterResource(id = R.drawable.youtube),
                    explain = "유튜브에서 보기",
                    onClick = {
                        OpenOtherApp(context).openYoutube(
                            packageName = (Contents.YOUTUBE_PACKAGE_NAME),
                            url = "https://www.youtube.com/@calmdownman_data/featured" // 침착맨 라이브 채널 주소
                        )
                        viewModel.setDialog(false)
                    }
                )
                Spacer(modifier = Modifier.width(20.dp))
                SelectionItem(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(color = colorResource(id = R.color.gray_bright_night)),
                    image = painterResource(id = R.drawable.twitch),
                    explain = "트위치에서 보기",
                    onClick = {
                        OpenOtherApp(context).openTwitch(
                            packageName = (Contents.TWITCH_CHANNEL_PACKAGE_NAME + channel?.name),
                            url = uiState.mainChannelInfo?.url ?: ""
                        )
                        viewModel.setDialog(false)
                    }
                )
            }
        }
    }
}

@Composable
fun SelectionItem(
    modifier: Modifier = Modifier,
    image: Painter,
    explain: String,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier
            .clickable { onClick() }
            .padding(12.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = image,
            contentDescription = explain,
            modifier = Modifier.size(74.dp)
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = explain,
            color = colorResource(id = R.color.item_color)
        )
    }
}