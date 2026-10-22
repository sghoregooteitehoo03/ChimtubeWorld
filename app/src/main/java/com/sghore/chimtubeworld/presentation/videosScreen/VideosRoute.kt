package com.sghore.chimtubeworld.presentation.videosScreen

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import com.sghore.chimtubeworld.core.common.Constants
import com.sghore.chimtubeworld.core.model.Video
import com.sghore.chimtubeworld.navigation.VideoSource
import com.sghore.chimtubeworld.other.OpenOtherApp
import com.sghore.chimtubeworld.presentation.selectBookmarkScreen.SelectBookmarkDialog

@Composable
fun VideosRoute(
    viewModel: VideosViewModel,
    title: String,
    source: VideoSource,
    onBack: () -> Unit,
    onBookmarkClick: (Video, Int) -> Unit
) {
    val uiState by viewModel.state.collectAsState()
    val context = LocalContext.current

    VideosScreen(
        uiState = uiState,
        title = title,
        onBack = onBack,
        onHelp = {
            Toast.makeText(
                context,
                "유튜브 및 트위치 영상을 해당 앱으로\n공유하면 북마크를 만드실 수 있습니다.",
                Toast.LENGTH_SHORT
            ).show()
        },
        onVideoClick = { video ->
            playVideo(
                viewModel = viewModel,
                context = context,
                video = video,
                source = source
            )
        },
        onBookmarkClick = onBookmarkClick
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
    source: VideoSource
) {
    val packageName = if (source == VideoSource.YOUTUBE) {
        // Youtube 패키지
        val youtubePackage = Constants.YOUTUBE_PACKAGE_NAME
        if (video.bookmarks.isEmpty()) {
            // 북마크가 없으면 바로 실행
            OpenOtherApp(context)
                .openYoutube(youtubePackage, video.url)
        }

        youtubePackage
    } else {
        // Twitch Video 패키지
        val twitchPackage = Constants.TWITCH_VIDEO_PACKAGE_NAME + video.id
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
