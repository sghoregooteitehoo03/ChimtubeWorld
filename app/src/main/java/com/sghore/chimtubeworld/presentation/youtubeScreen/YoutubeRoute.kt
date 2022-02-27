package com.sghore.chimtubeworld.presentation.youtubeScreen

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.gson.Gson
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
        onClick = { channelData ->
            val route =
                NavigationScreen.Videos.route +
                        "?typeImageRes=${R.drawable.youtube}&channelName=${channelData.name}&playlistId=${channelData.playlistId}&playlistName=${channelData.playlistName}"
            navController.navigate(route = route)
        }
    )
}