package com.sghore.chimtubeworld.presentation.playlistScreen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.sghore.chimtubeworld.R
import com.sghore.chimtubeworld.presentation.ui.NavigationScreen

@Composable
fun PlaylistRoute(
    navController: NavController,
    viewModel: PlaylistViewModel = hiltViewModel()
) {
    val uiState by viewModel.state.collectAsState()

    PlaylistScreen(
        uiState = uiState,
        onPlaylistClick = { playlist ->
            val route = NavigationScreen.Videos.route +
                    "?typeImageRes=${R.drawable.youtube}" +
                    "&playlistId=${playlist.id}" +
                    "&playlistName=${playlist.title}"

            navController.navigate(route = route)
        }
    )
}