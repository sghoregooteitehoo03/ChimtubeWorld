package com.sghore.chimtubeworld.presentation.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBackIos
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.plcoding.cryptocurrencyappyt.presentation.ui.theme.ChimtubeWorldTheme
import com.sghore.chimtubeworld.R
import com.sghore.chimtubeworld.data.model.Goods
import com.sghore.chimtubeworld.other.Contents
import com.sghore.chimtubeworld.presentation.cafeScreen.CafeRoute
import com.sghore.chimtubeworld.presentation.storeScreen.StoreRoute
import com.sghore.chimtubeworld.presentation.twitchScreen.TwitchRoute
import com.sghore.chimtubeworld.presentation.videosScreen.VideosRoute
import com.sghore.chimtubeworld.presentation.webToonScreen.WebToonRoute
import com.sghore.chimtubeworld.presentation.youtubeScreen.YoutubeRoute
import dagger.hilt.android.AndroidEntryPoint

// TODO:
//  . StoreDetailFragment 재구성 버그 수정 O
//  . 같은 채널에서 제공하는 영상인 경우 탭으로 표현
//  . 단기 방송 재생목록 만들기

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val gViewModel by viewModels<GlobalViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ChimtubeWorldTheme(
                darkTheme = isSystemInDarkTheme()
            ) {
                val navController = rememberNavController()
                val bottomMenu = listOf(
                    NavigationScreen.Youtube,
                    NavigationScreen.Twitch,
                    NavigationScreen.WebToon,
                    NavigationScreen.Cafe,
                    NavigationScreen.Store
                )

                Scaffold(
                    topBar = {
                        val navBackStackEntry by navController.currentBackStackEntryAsState()
                        // 현재 루트
                        val currentRoute =
                            navBackStackEntry?.destination?.route?.substringBefore("/")

                        TopAppBar(
                            elevation = 0.dp
                        ) {
                            if (NavigationScreen.Videos.route == currentRoute) {
                                Spacer(modifier = Modifier.width(14.dp))
                                Icon(
                                    imageVector = Icons.Default.ArrowBack,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .align(Alignment.CenterVertically)
                                        .size(24.dp)
                                        .clip(CircleShape)
                                        .clickable(
                                            interactionSource = remember { MutableInteractionSource() },
                                            indication = rememberRipple()
                                        ) {
                                            navController.navigateUp()
                                        }
                                )
                            } else {
                                Text(
                                    text = "CHIMHA",
                                    color = colorResource(id = R.color.item_color),
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    },
                    bottomBar = {
                        BottomNavigation(
                            backgroundColor = colorResource(id = R.color.default_background_color)
                        ) {
                            val navBackStackEntry by navController.currentBackStackEntryAsState()
                            val currentDestination = navBackStackEntry?.destination
                            val currentRoute = currentDestination?.route?.substringBefore("/")

                            bottomMenu.forEach { menu ->
                                // 아이콘 선택 여부
                                val isSelected =
                                    if (currentRoute == NavigationScreen.Videos.route) {
                                        (menu.route == NavigationScreen.Youtube.route && navController.backQueue.size == 3) ||
                                                (menu.route == NavigationScreen.Twitch.route && navController.backQueue.size == 4)
                                    } else {
                                        currentDestination?.hierarchy?.any { it.route == menu.route } == true
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
                ) { innerPadding ->
                    Column {
                        Divider(
                            color = colorResource(id = R.color.gray_bright_night)
                        )

                        NavHost(
                            navController = navController,
                            startDestination = NavigationScreen.Youtube.route,
                            modifier = Modifier.padding(innerPadding)
                        ) {
                            composable(route = NavigationScreen.Youtube.route) {
                                YoutubeRoute(navController = navController)
                            }
                            composable(route = NavigationScreen.Twitch.route) {
                                TwitchRoute(navController = navController)
                            }
                            composable(route = NavigationScreen.WebToon.route) {
                                WebToonRoute()
                            }
                            composable(route = NavigationScreen.Cafe.route) {
                                CafeRoute()
                            }
                            composable(route = NavigationScreen.Store.route) {
                                StoreRoute(onGoodsClick = ::moveViewPagerScreen)
                            }
                            composable(
                                route = NavigationScreen.Videos.route + "/{typeImageRes}/{channelName}/{channelId}",
                                arguments = listOf(
                                    navArgument("typeImageRes") { type = NavType.IntType },
                                    navArgument("channelName") { type = NavType.StringType },
                                    navArgument("channelId") { type = NavType.StringType }
                                )
                            ) { entry ->
                                VideosRoute(
                                    gViewModel = gViewModel,
                                    navController = navController,
                                    channelName = entry.arguments?.getString("channelName") ?: "",
                                    typeImageRes = entry.arguments?.getInt("typeImageRes")
                                        ?: R.drawable.youtube
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    private fun moveViewPagerScreen(goodsList: List<Goods>, selectedPos: Int) {
        startActivity(
            Intent(this@MainActivity, ViewPagerActivity::class.java)
                .apply {
                    this.putExtra(
                        Contents.KEY_GOODS_LIST,
                        goodsList.toTypedArray()
                    )
                    this.putExtra(
                        Contents.KEY_SELECTED_POS,
                        selectedPos
                    )
                    this.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                }
        )
    }
}