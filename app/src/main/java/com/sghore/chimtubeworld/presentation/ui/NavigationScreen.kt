package com.sghore.chimtubeworld.presentation.ui

import com.sghore.chimtubeworld.core.designsystem.R as DesignSystemR

sealed class NavigationScreen(
    val route: String,
    val selectedIcon: Int = -1,
    val unSelectedIcon: Int = -1
) {
    object Youtube : NavigationScreen(
        route = "Youtube",
        selectedIcon = DesignSystemR.drawable.youtube,
        unSelectedIcon = DesignSystemR.drawable.youtube_darker
    )

    object Twitch : NavigationScreen(
        route = "Twitch",
        selectedIcon = DesignSystemR.drawable.chzzk,
        unSelectedIcon = DesignSystemR.drawable.chzzk_darker
    )

    object WebToon : NavigationScreen(
        route = "Webtoon",
        selectedIcon = DesignSystemR.drawable.webtoon,
        unSelectedIcon = DesignSystemR.drawable.webtoon_darker
    )

    object Cafe : NavigationScreen(
        route = "Cafe",
        selectedIcon = DesignSystemR.drawable.cafe,
        unSelectedIcon = DesignSystemR.drawable.cafe_darker
    )

    object Store : NavigationScreen(
        route = "Store",
        selectedIcon = DesignSystemR.drawable.store,
        unSelectedIcon = DesignSystemR.drawable.store_darker
    )

    object Playlists : NavigationScreen(
        route = "Playlists"
    )

    object Videos : NavigationScreen(
        route = "Videos"
    )

    object AddBookmark : NavigationScreen(
        route = "AddBookmark"
    )

    object EditBookmark : NavigationScreen(
        route = "EditBookmark"
    )
}
