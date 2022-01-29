package com.sghore.chimtubeworld.presentation.videosScreen

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.sghore.chimtubeworld.data.model.Video
import com.sghore.chimtubeworld.presentation.ui.GlobalViewModel

@Composable
fun VideosRoute(
    viewModel: VideosViewModel,
    gViewModel: GlobalViewModel,
    navController: NavController,
    args: VideosFragmentArgs,
    videoClick: (Video) -> Unit
) {
    val uiState by viewModel.state.collectAsState()
    val context = LocalContext.current

    gViewModel.bookmarkData.value?.let {
        viewModel.changeVideoBookmarks(it)
        gViewModel.bookmarkData.value = null
    }

    LaunchedEffect(key1 = uiState.toastMsg) {
        val msg = uiState.toastMsg

        if (msg.isNotEmpty()) {
            Toast.makeText(context, msg, Toast.LENGTH_SHORT)
                .show()
            viewModel.setMessage("")
        }
    }

    VideosScreen(
        uiState = uiState,
        channelName = args.channelName,
        videoTypeImage = args.typeImageRes,
        onHelpClick = {
            viewModel.setMessage("유튜브 및 트위치 영상을 해당 앱으로\n공유하면 북마크를 만드실 수 있습니다.")
        },
        onVideoClick = videoClick,
        onBookmarkClick = { video, pos ->
            val directions = VideosFragmentDirections.actionVideosFragmentToEditBookmarkFragment(
                args.typeImageRes,
                pos
            )

            gViewModel.videoData.value = video
            navController.navigate(directions)
        }
    )
}