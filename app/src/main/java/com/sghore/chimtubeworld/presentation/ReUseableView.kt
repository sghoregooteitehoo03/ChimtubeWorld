package com.sghore.chimtubeworld.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import com.sghore.chimtubeworld.R
import com.sghore.chimtubeworld.presentation.ui.NavigationScreen

@Composable
fun TitleTextWithExplain(
    title: String,
    explain: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.Bottom
    ) {
        Text(
            text = title,
            color = colorResource(id = R.color.item_color),
            fontSize = 22.sp
        )
        Spacer(Modifier.width(6.dp))
        Text(
            text = explain,
            color = colorResource(id = R.color.default_text_color),
            fontSize = 16.sp
        )
    }
}

@Composable
fun <T> RowList(
    list: List<T>,
    spanCount: Int,
    contentPaddingValue: Dp,
    itemPaddingValue: Dp,
    headerItem: @Composable () -> Unit,
    listItem: @Composable (Int, Modifier) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        contentPadding = PaddingValues(contentPaddingValue)
    ) {
        val itemCount = if (list.size % spanCount == 0) {
            list.size / spanCount
        } else {
            list.size / spanCount + 1
        }

        item {
            headerItem()
        }

        items(itemCount) { index ->
            RowItemCollocate(
                rowIndex = index,
                list = list,
                spanCount = spanCount,
                paddingValue = itemPaddingValue,
                content = listItem,
                modifier = modifier
            )
        }
    }
}

@Composable
fun <T> RowItemCollocate(
    rowIndex: Int,
    list: List<T>,
    spanCount: Int,
    paddingValue: Dp,
    content: @Composable (Int, Modifier) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Row {
            for (index in 0 until spanCount) {
                if (list.size >= rowIndex * spanCount + (index + 1)) {
                    content(
                        rowIndex * spanCount + index,
                        Modifier.weight(1f)
                    )

                    if (index != spanCount - 1) {
                        Spacer(modifier = Modifier.width(paddingValue))
                    }
                } else {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
        Spacer(modifier = Modifier.height(paddingValue))
    }
}


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
        BottomNavigation(
            backgroundColor = colorResource(id = R.color.default_background_color),
        ) {
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

                BottomNavigationItem(
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