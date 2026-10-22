package com.sghore.chimtubeworld.feature.webtoon.api.navigation

import androidx.navigation3.runtime.NavKey
import com.sghore.chimtubeworld.core.navigation.Navigator
import kotlinx.serialization.Serializable

@Serializable
data object WebtoonKey : NavKey

fun Navigator.navigateToWebtoon() {
    navigate(WebtoonKey)
}
