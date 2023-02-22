package com.sghore.chimtubeworld.presentation.cafeScreen

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.sghore.chimtubeworld.other.OpenOtherApp

// TODO
//  . 침하하 바로가기 버튼 추가
@Composable
fun CafeRoute(
    viewModel: CafeViewModel = hiltViewModel()
) {
    val uiState by viewModel.state.collectAsState()
    val context = LocalContext.current

    CafeScreen(
        uiState = uiState,
        onCafeBannerClick = { url ->
            openCustomTabs(
                context = context,
                url = url
            )
        },
        onCafeCategoryClick = viewModel::changeCategory,
        onCafePostClick = { post ->
            viewModel.readPost(post = post!!)
            openCustomTabs(
                context = context,
                url = post.url ?: ""
            )
        }
    )
}

private fun openCustomTabs(context: Context, url: String) {
    OpenOtherApp(context = context)
        .openCustomTabs(url = url)
}