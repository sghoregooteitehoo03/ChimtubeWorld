package com.sghore.chimtubeworld.feature.chzzk.impl.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.sghore.chimtubeworld.feature.chzzk.api.navigation.ChzzkKey
import com.sghore.chimtubeworld.feature.chzzk.impl.chzzk_screen.TwitchRoute

fun EntryProviderScope<NavKey>.chzzkEntry() {
    entry<ChzzkKey> {
        TwitchRoute()
    }
}
