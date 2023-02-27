package com.sghore.chimtubeworld.presentation.youtubeScreen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.sghore.chimtubeworld.presentation.ui.NavigationScreen

@Composable
fun YoutubeRoute(
    navController: NavController,
    viewModel: YoutubeViewModel = hiltViewModel(),
) {
    val uiState by viewModel.state.collectAsState()

    YoutubeScreen(
        uiState = uiState,
        onChannelClick = { channelData ->
            val route = if (channelData.type == 0) {
                NavigationScreen.Playlists.route +
                        "?channelName=${channelData.name}&channelId=${channelData.id.split("|")[0]}&playlistId=${channelData.playlistId}"
            } else {
                NavigationScreen.Playlists.route +
                        "?channelName=${channelData.name}&playlistId=${channelData.playlistId}"
            }

            navController.navigate(route = route)
        }
    )
}