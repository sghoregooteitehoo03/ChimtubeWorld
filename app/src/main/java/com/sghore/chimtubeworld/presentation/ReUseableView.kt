package com.sghore.chimtubeworld.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import com.sghore.chimtubeworld.presentation.ui.NavigationScreen
import com.sghore.chimtubeworld.core.designsystem.R as DesignSystemR

fun TopAppBarNavigationItem(
    currentRoute: String?,
    content: @Composable () -> Unit,
): (@Composable (() -> Unit))? {
    return when (currentRoute) {
        NavigationScreen.Playlists.route, NavigationScreen.Videos.route,
        NavigationScreen.AddBookmark.route, NavigationScreen.EditBookmark.route -> {
            content
        }

        else -> null
    }
}

@Composable
fun BottomNavigationBar(
    isHide: Boolean,
    navController: NavHostController,
    bottomMenu: List<NavigationScreen>,
    currentDestination: NavDestination?,
    currentRoute: String?,
    previousRoute: String?
) {

    if (!isHide) {
        NavigationBar(
            modifier = Modifier.height(56.dp).shadow(8.dp),
            containerColor = colorResource(id = DesignSystemR.color.default_background_color),
            contentColor = MaterialTheme.colorScheme.onBackground,
            tonalElevation = 0.dp,
            windowInsets = WindowInsets(0, 0, 0, 0),
        ) {
            // M3 spaces items by 8dp; the old bar divided its full width equally.
            Row(Modifier.fillMaxSize()) {
                bottomMenu.forEach { menu ->
                    // 아이콘 선택 여부
                    val isSelected = when (currentRoute) {
                        NavigationScreen.Playlists.route -> {
                            menu.route == NavigationScreen.Youtube.route
                                    && previousRoute == NavigationScreen.Youtube.route
                        }

                        NavigationScreen.Videos.route -> {
                            (menu.route == NavigationScreen.Youtube.route
                                    && previousRoute == NavigationScreen.Playlists.route) ||
                                    (menu.route == NavigationScreen.Twitch.route
                                            && previousRoute == NavigationScreen.Twitch.route)
                        }

                        else -> {
                            currentDestination?.hierarchy?.any { it.route == menu.route } == true
                        }
                    }

                    NavigationBarItem(
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.onBackground,
                            unselectedIconColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                            indicatorColor = Color.Transparent
                        ),
                        icon = {
                            Image(
                                painter = painterResource(
                                    id = if (isSelected) {
                                        menu.selectedIcon
                                    } else {
                                        menu.unSelectedIcon
                                    }
                                ),
                                contentDescription = null
                            )
                        },
                        selected = isSelected,
                        onClick = {
                            navController.navigate(menu.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }

                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    }
}
