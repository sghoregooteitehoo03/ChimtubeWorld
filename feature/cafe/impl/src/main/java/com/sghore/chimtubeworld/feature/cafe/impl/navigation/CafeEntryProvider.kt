package com.sghore.chimtubeworld.feature.cafe.impl.navigation

import androidx.compose.ui.platform.LocalContext
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.sghore.chimtubeworld.core.ui.OpenOtherApp
import com.sghore.chimtubeworld.feature.cafe.api.navigation.CafeKey
import com.sghore.chimtubeworld.feature.cafe.impl.cafe_screen.CafeRoute
import com.sghore.chimtubeworld.feature.cafe.impl.cafe_screen.CafeUiSideEffect

fun EntryProviderScope<NavKey>.cafeEntry() {
    entry<CafeKey> {
        val context = LocalContext.current

        CafeRoute(
            onSideEffect = { sideEffect ->
                when (sideEffect) {
                    is CafeUiSideEffect.OpenUrl -> {
                        OpenOtherApp(context).openCustomTabs(sideEffect.url)
                    }
                }
            }
        )
    }
}
