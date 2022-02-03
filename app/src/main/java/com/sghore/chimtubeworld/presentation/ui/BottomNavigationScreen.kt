package com.sghore.chimtubeworld.presentation.ui

import com.sghore.chimtubeworld.R

sealed class BottomNavigationScreen(
    val route: String,
    val selectedIcon: Int,
    val unSelectedIcon: Int
) {
    object Youtube : BottomNavigationScreen(
        route = "Youtube",
        selectedIcon = R.drawable.youtube,
        unSelectedIcon = R.drawable.youtube_darker
    )

    object Twitch : BottomNavigationScreen(
        route = "Twitch",
        selectedIcon = R.drawable.twitch,
        unSelectedIcon = R.drawable.twitch_darker
    )

    object WebToon : BottomNavigationScreen(
        route = "Webtoon",
        selectedIcon = R.drawable.webtoon,
        unSelectedIcon = R.drawable.webtoon_darker
    )

    object Cafe : BottomNavigationScreen(
        route = "Cafe",
        selectedIcon = R.drawable.cafe,
        unSelectedIcon = R.drawable.cafe_darker
    )

    object Store : BottomNavigationScreen(
        route = "Store",
        selectedIcon = R.drawable.store,
        unSelectedIcon = R.drawable.store_darker
    )
}
