package com.sghore.chimtubeworld.navigation

import androidx.annotation.DrawableRes
import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable
import com.sghore.chimtubeworld.core.designsystem.R as DesignSystemR

@Serializable
data object ChzzkKey : NavKey

@Serializable
data object WebtoonKey : NavKey

@Serializable
data object CafeKey : NavKey

@Serializable
data object StoreKey : NavKey

data class TopLevelDestination(
    val key: NavKey,
    @param:DrawableRes val selectedIconRes: Int,
    @param:DrawableRes val unselectedIconRes: Int
)

val TOP_LEVEL_DESTINATIONS = listOf(
    TopLevelDestination(YoutubeKey, DesignSystemR.drawable.youtube, DesignSystemR.drawable.youtube_darker),
    TopLevelDestination(ChzzkKey, DesignSystemR.drawable.chzzk, DesignSystemR.drawable.chzzk_darker),
    TopLevelDestination(WebtoonKey, DesignSystemR.drawable.webtoon, DesignSystemR.drawable.webtoon_darker),
    TopLevelDestination(CafeKey, DesignSystemR.drawable.cafe, DesignSystemR.drawable.cafe_darker),
    TopLevelDestination(StoreKey, DesignSystemR.drawable.store, DesignSystemR.drawable.store_darker)
)
