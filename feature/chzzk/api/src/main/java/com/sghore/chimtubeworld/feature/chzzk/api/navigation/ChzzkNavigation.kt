package com.sghore.chimtubeworld.feature.chzzk.api.navigation

import androidx.navigation3.runtime.NavKey
import com.sghore.chimtubeworld.core.navigation.Navigator
import kotlinx.serialization.Serializable

@Serializable
data object ChzzkKey : NavKey

fun Navigator.navigateToChzzk() {
    navigate(ChzzkKey)
}
