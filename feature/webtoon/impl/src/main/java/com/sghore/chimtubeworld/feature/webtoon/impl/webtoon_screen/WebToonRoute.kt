package com.sghore.chimtubeworld.feature.webtoon.impl.webtoon_screen

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.sghore.chimtubeworld.core.common.Constants
import com.sghore.chimtubeworld.core.ui.OpenOtherApp

@Composable
internal fun WebToonRoute(
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
