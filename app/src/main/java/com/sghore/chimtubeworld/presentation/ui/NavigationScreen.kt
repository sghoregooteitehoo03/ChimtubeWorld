package com.sghore.chimtubeworld.presentation.ui

import com.sghore.chimtubeworld.R

sealed class NavigationScreen(
    val route: String,
    val selectedIcon: Int = -1,
    val unSelectedIcon: Int = -1
) {
    object Youtube : NavigationScreen(
        route = "Youtube",
        selectedIcon = R.drawable.youtube,
        unSelectedIcon = R.drawable.youtube_darker
    )

    object Twitch : NavigationScreen(
        route = "Twitch",
        selectedIcon = R.drawable.chzzk,
        unSelectedIcon = R.drawable.chzzk_darker
    )

    object WebToon : NavigationScreen(
        route = "Webtoon",
        selectedIcon = R.drawable.webtoon,
        unSelectedIcon = R.drawable.webtoon_darker
    )

    object Cafe : NavigationScreen(
        route = "Cafe",
        selectedIcon = R.drawable.cafe,
        unSelectedIcon = R.drawable.cafe_darker
    )

    object Store : NavigationScreen(
        route = "Store",
        selectedIcon = R.drawable.store,
        unSelectedIcon = R.drawable.store_darker
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
