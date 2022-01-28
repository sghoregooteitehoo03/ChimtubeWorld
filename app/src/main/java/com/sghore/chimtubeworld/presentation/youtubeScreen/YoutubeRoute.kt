package com.sghore.chimtubeworld.presentation.youtubeScreen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import com.sghore.chimtubeworld.R

@Composable
fun YoutubeRoute(
    viewModel: YoutubeViewModel,
    navController: NavController
) {
    val uiState by viewModel.state.collectAsState()
    YoutubeScreen(
        uiState = uiState,
        onClick = { channelData ->
            val directions = YoutubeFragmentDirections
                .actionYoutubeFragmentToVideosFragment(
                    channelName = channelData.name,
                    channelId = channelData.id.split("|")[1],
                    typeImageRes = R.drawable.youtube
                )
            navController.navigate(directions)
        }
    )
}