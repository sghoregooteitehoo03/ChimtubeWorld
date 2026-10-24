package com.sghore.chimtubeworld.feature.cafe.api.navigation

import androidx.navigation3.runtime.NavKey
import com.sghore.chimtubeworld.core.navigation.Navigator
import kotlinx.serialization.Serializable

@Serializable
data object CafeKey : NavKey

fun Navigator.navigateToCafe() {
    navigate(CafeKey)
}
