package com.sghore.chimtubeworld.presentation.webToonScreen

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.sghore.chimtubeworld.other.Constants
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
                    packageName = Constants.NAVER_WEBTOON_PACKAGE_NAME,
                    scheme = Constants.NAVER_WEBTOON_SCHEME + it.id,
                    url = it.url
                )
        }
    )
}