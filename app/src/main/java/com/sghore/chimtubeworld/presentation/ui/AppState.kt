package com.sghore.chimtubeworld.presentation.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import com.sghore.chimtubeworld.core.navigation.NavigationState
import com.sghore.chimtubeworld.core.navigation.Navigator
import com.sghore.chimtubeworld.core.navigation.rememberNavigationState
import com.sghore.chimtubeworld.navigation.TOP_LEVEL_DESTINATIONS
import com.sghore.chimtubeworld.feature.youtube.api.navigation.YoutubeKey

@Stable
internal class AppState(val navigationState: NavigationState) {
    val navigator = Navigator(navigationState)
}

@Composable
internal fun rememberAppState(): AppState {
    val topLevelKeys = remember { TOP_LEVEL_DESTINATIONS.map { it.key }.toSet() }
    val navigationState = rememberNavigationState(YoutubeKey, topLevelKeys)
    return remember(navigationState) { AppState(navigationState) }
}
