package com.sghore.chimtubeworld.feature.store.impl.navigation

import androidx.compose.ui.platform.LocalContext
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.sghore.chimtubeworld.core.ui.OpenOtherApp
import com.sghore.chimtubeworld.feature.store.api.navigation.StoreKey
import com.sghore.chimtubeworld.feature.store.impl.store_screen.StoreRoute
import com.sghore.chimtubeworld.feature.store.impl.store_screen.StoreUiSideEffect

fun EntryProviderScope<NavKey>.storeEntry() {
    entry<StoreKey> {
        val context = LocalContext.current

        StoreRoute(
            onSideEffect = { sideEffect ->
                when (sideEffect) {
                    is StoreUiSideEffect.OpenUrl -> {
                        OpenOtherApp(context).openCustomTabs(sideEffect.url)
                    }
                }
            }
        )
    }
}
