package com.sghore.chimtubeworld.feature.store.api.navigation

import androidx.navigation3.runtime.NavKey
import com.sghore.chimtubeworld.core.navigation.Navigator
import kotlinx.serialization.Serializable

@Serializable
data object StoreKey : NavKey

fun Navigator.navigateToStore() {
    navigate(StoreKey)
}
