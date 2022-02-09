package com.sghore.chimtubeworld.presentation.videosScreen

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.sghore.chimtubeworld.R
import com.sghore.chimtubeworld.data.model.Video
import com.sghore.chimtubeworld.other.Contents
import com.sghore.chimtubeworld.other.OpenOtherApp
import com.sghore.chimtubeworld.presentation.selectBookmarkScreen.SelectBookmarkDialog
import com.sghore.chimtubeworld.presentation.ui.GlobalViewModel
import com.sghore.chimtubeworld.presentation.ui.NavigationScreen

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

    VideosScreen(
        uiState = uiState,
        context = context,
        channelName = channelName,
        videoTypeImage = typeImageRes,
        onHelpClick = {
            viewModel.setMessage("유튜브 및 트위치 영상을 해당 앱으로\n공유하면 북마크를 만드실 수 있습니다.")
        },
        onToastClear = {
            viewModel.setMessage("")
        },
        onVideoClick = { video ->
            playVideo(
                viewModel = viewModel,
                context = context,
                video = video,
                typeImageRes = typeImageRes
            )
        },
        onBookmarkClick = { video, pos ->
            val route =
                NavigationScreen.EditBookmark.route + "?typeImageRes=${typeImageRes}&pos=${pos}&video=${
                    Uri.encode(Gson().toJson(video))
                }"
            navController.navigate(route = route)
        }
    )

    if (uiState.isDialogOpen) {
        // 다이얼로그 표시
        SelectBookmarkDialog(
            packageName = viewModel.packageName,
            video = viewModel.video!!,
            onDismissRequest = {
                viewModel.setDialogOpen(
                    _packageName = "",
                    _video = null,
                    isOpen = false
                )
            }
        )
    }
}

// 영상을 실행시킴
private fun playVideo(
    viewModel: VideosViewModel,
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

    if (video.bookmarks.isNotEmpty()) {
        // 북마크가 존재하면 선택 화면 표시
        viewModel.setDialogOpen(
            _packageName = packageName,
            _video = video,
            isOpen = true
        )
    }
}