package com.sghore.chimtubeworld.presentation.twitchScreen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.sghore.chimtubeworld.R
import com.sghore.chimtubeworld.other.Contents
import com.sghore.chimtubeworld.other.OpenOtherApp
import com.sghore.chimtubeworld.presentation.ui.NavigationScreen

// TODO
//  . 배도라지 멤버 모두 라이브 중인지 보여주는 기능
//  . 침착맨 라이브 중 유튜브 트위치 선택 다이얼로그 구현
@Composable
fun TwitchRoute(
    navController: NavController,
    viewModel: TwitchViewModel = hiltViewModel()
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
        onTwitchCrewChannelClick = { channelData ->
            // 동영상 리스트 화면으로 이동
            val route = NavigationScreen.Videos.route +
                    "?typeImageRes=${R.drawable.twitch}" +
                    "&playlistId=${channelData.id}" +
                    "&playlistName=${channelData.name}"

            navController.navigate(route = route)
        }
    )
}