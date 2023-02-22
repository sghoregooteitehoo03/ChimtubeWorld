package com.sghore.chimtubeworld.presentation.webToonScreen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.sghore.chimtubeworld.other.Contents
import com.sghore.chimtubeworld.other.OpenOtherApp

@Composable
fun WebToonRoute(
    viewModel: WebToonViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    WebToonScreen(
        webToonInfos = viewModel.webToonInfos,
        onWebToonClick = {
            OpenOtherApp(context = context)
                .openNaverWebToon(
                    packageName = Contents.NAVER_WEBTOON_PACKAGE_NAME,
                    scheme = Contents.NAVER_WEBTOON_SCHEME + it.id,
                    url = it.url
                )
        }
    )
}