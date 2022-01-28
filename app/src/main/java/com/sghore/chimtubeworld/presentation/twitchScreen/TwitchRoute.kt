package com.sghore.chimtubeworld.presentation.twitchScreen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.sghore.chimtubeworld.R
import com.sghore.chimtubeworld.other.Contents
import com.sghore.chimtubeworld.other.OpenOtherApp

@Composable
fun TwitchRoute(
    viewModel: TwitchViewModel,
    navController: NavController
) {
    val uiState by viewModel.state.collectAsState()
    val context = LocalContext.current

    TwitchScreen(
        uiState = uiState,
        onMainChannelClick = { channel ->
            // 트위치 채널로 이동
            OpenOtherApp(context).openTwitch(
                packageName = Contents.TWITCH_CHANNEL_PACKAGE_NAME + channel!!.name,
                url = channel.url
            )
        },
        onTwitchCrewChannelClick = { channel ->
            // 동영상 리스트 화면으로 이동
            val directions = TwitchFragmentDirections
                .actionTwitchFragmentToVideosFragment(
                    channelName = channel!!.name,
                    channelId = channel.id,
                    typeImageRes = R.drawable.twitch
                )
            navController.navigate(directions)
        }
    )
}