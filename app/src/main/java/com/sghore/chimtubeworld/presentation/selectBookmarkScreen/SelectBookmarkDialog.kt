package com.sghore.chimtubeworld.presentation.selectBookmarkScreen

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.window.Dialog
import com.sghore.chimtubeworld.data.model.Video
import com.sghore.chimtubeworld.other.Contents
import com.sghore.chimtubeworld.other.OpenOtherApp

@Composable
fun SelectBookmarkDialog(
    packageName: String,
    video: Video,
    onDismissRequest: () -> Unit
) {
    val context = LocalContext.current

    Dialog(onDismissRequest = onDismissRequest) {
        SelectBookmarkScreen(
            bookmarkList = video.bookmarks,
            onBookmarkClick = { videoPosition ->
                clickBookmark(
                    context = context,
                    video = video,
                    videoPosition = videoPosition,
                    packageName = packageName
                )
                onDismissRequest() // 다이얼로그 닫기
            }
        )
    }
}

// 북마크 클릭 이벤트
private fun clickBookmark(
    context: Context,
    video: Video,
    videoPosition: Long,
    packageName: String
) {
    val seconds = getSecondsFromPosition(videoPosition)
    val url = getUrlWithSeconds(seconds = seconds, video = video)

    openApplication(context = context, url = url, packageName = packageName)
}

// 다른 어플리케이션으로 영상을 실행
private fun openApplication(
    context: Context,
    url: String,
    packageName: String
) {
    if (packageName == Contents.YOUTUBE_PACKAGE_NAME) {
        OpenOtherApp(context).openYoutube(
            packageName,
            url
        )
    } else {
        OpenOtherApp(context).openTwitch(
            packageName,
            url
        )
    }
}

// 시간이 포함된 url을 반환
private fun getUrlWithSeconds(seconds: String, video: Video): String {
    val url = video.url

    return if (url.contains("?")) {
        // 쿼리문이 존재할 때
        "${url}&t=$seconds"
    } else {
        // 쿼리문이 존재하지 않을 때
        "${url}?t=$seconds"
    }
}

// 시간 데이터를 초로 변환
private fun getSecondsFromPosition(videoPosition: Long) =
    "${(videoPosition + 32400000) / 1000}s"