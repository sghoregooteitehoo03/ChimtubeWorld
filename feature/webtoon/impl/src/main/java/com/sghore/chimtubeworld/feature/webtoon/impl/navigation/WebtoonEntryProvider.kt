package com.sghore.chimtubeworld.feature.webtoon.impl.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.sghore.chimtubeworld.feature.webtoon.api.navigation.WebtoonKey
import com.sghore.chimtubeworld.feature.webtoon.impl.webtoon_screen.WebToonRoute

fun EntryProviderScope<NavKey>.webtoonEntry() {
    entry<WebtoonKey> {
        WebToonRoute()
    }
}
