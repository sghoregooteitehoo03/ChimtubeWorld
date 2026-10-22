package com.sghore.chimtubeworld.feature.store.impl.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.sghore.chimtubeworld.feature.store.api.navigation.StoreKey
import com.sghore.chimtubeworld.feature.store.impl.store_screen.StoreRoute

fun EntryProviderScope<NavKey>.storeEntry() {
    entry<StoreKey> {
        StoreRoute()
    }
}
