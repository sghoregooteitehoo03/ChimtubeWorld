package com.sghore.chimtubeworld.feature.chzzk.impl.navigation

import androidx.compose.ui.platform.LocalContext
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.sghore.chimtubeworld.core.common.Constants
import com.sghore.chimtubeworld.core.ui.OpenOtherApp
import com.sghore.chimtubeworld.feature.chzzk.api.navigation.ChzzkKey
import com.sghore.chimtubeworld.feature.chzzk.impl.chzzk_screen.ChzzkRoute
import com.sghore.chimtubeworld.feature.chzzk.impl.chzzk_screen.ChzzkUiSideEffect

fun EntryProviderScope<NavKey>.chzzkEntry() {
    entry<ChzzkKey> {
        val context = LocalContext.current

        ChzzkRoute(
            onSideEffect = { sideEffect ->
                when (sideEffect) {
                    is ChzzkUiSideEffect.OpenYoutube -> {
                        OpenOtherApp(context).openYoutube(
                            packageName = Constants.YOUTUBE_PACKAGE_NAME,
                            url = sideEffect.url
                        )
                    }

                    is ChzzkUiSideEffect.OpenUrl -> {
                        OpenOtherApp(context).openCustomTabs(sideEffect.url)
                    }
                }
            }
        )
    }
}
