package com.sghore.chimtubeworld.presentation.webToonScreen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.sghore.chimtubeworld.other.Contents
import com.sghore.chimtubeworld.other.OpenOtherApp

// TODO
//  . 웹툰 화면 정적으로 구현(
//  1. 들어갈 이미지 미리 세팅해놓기 O
//  2. 이미지 넣어보기 O
//  3. 웹툰 이동 확인하기 O
//  )
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