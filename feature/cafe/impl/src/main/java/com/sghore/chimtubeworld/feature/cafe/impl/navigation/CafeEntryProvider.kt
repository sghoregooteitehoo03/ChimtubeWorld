package com.sghore.chimtubeworld.feature.cafe.impl.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.sghore.chimtubeworld.feature.cafe.api.navigation.CafeKey
import com.sghore.chimtubeworld.feature.cafe.impl.cafe_screen.CafeRoute

fun EntryProviderScope<NavKey>.cafeEntry() {
    entry<CafeKey> {
        CafeRoute()
    }
}
