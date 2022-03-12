package com.sghore.chimtubeworld.presentation.youtubeScreen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.sghore.chimtubeworld.R
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
            val route =
                NavigationScreen.Videos.route +
                        "?typeImageRes=${R.drawable.youtube}" +
                        "&channelName=${channelData.name}" +
                        "&playlistId=${channelData.playlistId}" +
                        "&playlistName=${channelData.playlistName}"

            navController.navigate(route = route)
        }
    )
}