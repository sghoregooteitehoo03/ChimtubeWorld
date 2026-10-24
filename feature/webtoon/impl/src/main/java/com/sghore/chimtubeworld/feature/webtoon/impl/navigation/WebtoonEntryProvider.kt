package com.sghore.chimtubeworld.feature.webtoon.impl.navigation

import androidx.compose.ui.platform.LocalContext
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.sghore.chimtubeworld.core.common.Constants
import com.sghore.chimtubeworld.core.ui.OpenOtherApp
import com.sghore.chimtubeworld.feature.webtoon.api.navigation.WebtoonKey
import com.sghore.chimtubeworld.feature.webtoon.impl.webtoon_screen.WebtoonRoute
import com.sghore.chimtubeworld.feature.webtoon.impl.webtoon_screen.WebtoonUiSideEffect

fun EntryProviderScope<NavKey>.webtoonEntry() {
    entry<WebtoonKey> {
        val context = LocalContext.current

        WebtoonRoute(
            onSideEffect = { sideEffect ->
                when (sideEffect) {
                    is WebtoonUiSideEffect.OpenWebtoon -> {
                        OpenOtherApp(context).openNaverWebToon(
                            packageName = Constants.NAVER_WEBTOON_PACKAGE_NAME,
                            scheme = Constants.NAVER_WEBTOON_SCHEME + sideEffect.id,
                            url = sideEffect.url
                        )
                    }
                }
            }
        )
    }
}
