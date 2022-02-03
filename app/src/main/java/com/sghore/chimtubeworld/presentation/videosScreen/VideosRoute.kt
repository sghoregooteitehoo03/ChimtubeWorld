package com.sghore.chimtubeworld.presentation.videosScreen

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.sghore.chimtubeworld.R
import com.sghore.chimtubeworld.data.model.Video
import com.sghore.chimtubeworld.other.Contents
import com.sghore.chimtubeworld.other.OpenOtherApp
import com.sghore.chimtubeworld.presentation.ui.GlobalViewModel

@Composable
fun VideosRoute(
    viewModel: VideosViewModel = hiltViewModel(),
    gViewModel: GlobalViewModel,
    navController: NavController,
    channelName: String,
    typeImageRes: Int
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
        channelName = channelName,
        videoTypeImage = typeImageRes,
        onHelpClick = {
            viewModel.setMessage("유튜브 및 트위치 영상을 해당 앱으로\n공유하면 북마크를 만드실 수 있습니다.")
        },
        onVideoClick = { video ->
            playVideo(
                context = context,
                video = video,
                typeImageRes = typeImageRes
            )
        },
        onBookmarkClick = { video, pos ->
            val directions = VideosFragmentDirections.actionVideosFragmentToEditBookmarkFragment(
                typeImageRes,
                pos
            )

            gViewModel.videoData.value = video
            navController.navigate(directions)
        }
    )
}

// 영상을 실행시킴
private fun playVideo(
    context: Context,
    video: Video,
    typeImageRes: Int
) {
    val packageName = if (typeImageRes == R.drawable.youtube) {
        // Youtube 패키지
        val youtubePackage = Contents.YOUTUBE_PACKAGE_NAME
        if (video.bookmarks.isEmpty()) {
            // 북마크가 없으면 바로 실행
            OpenOtherApp(context)
                .openYoutube(youtubePackage, video.url)
        }

        youtubePackage
    } else {
        // Twitch Video 패키지
        val twitchPackage = Contents.TWITCH_VIDEO_PACKAGE_NAME + video.id
        if (video.bookmarks.isEmpty()) {
            // 북마크가 없으면 바로 실행
            OpenOtherApp(context)
                .openTwitch(twitchPackage, video.url)
        }

        twitchPackage
    }

//    if (video.bookmarks.isNotEmpty()) {
//        // 북마크가 존재하면 선택 화면 표시
//        SelectBookmarkDialog(packageName, video).show(
//            requireActivity().supportFragmentManager,
//            "SelectPositionDialog"
//        )
//    }
}