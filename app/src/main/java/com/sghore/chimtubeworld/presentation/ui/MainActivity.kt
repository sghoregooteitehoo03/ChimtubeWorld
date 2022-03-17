package com.sghore.chimtubeworld.presentation.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Help
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.plcoding.cryptocurrencyappyt.presentation.ui.theme.ChimtubeWorldTheme
import com.sghore.chimtubeworld.R
import com.sghore.chimtubeworld.data.model.Goods
import com.sghore.chimtubeworld.other.Contents
import com.sghore.chimtubeworld.presentation.BottomNavigationBar
import com.sghore.chimtubeworld.presentation.TopAppBarNavigationItem
import com.sghore.chimtubeworld.presentation.bookmarkScreen.AddBookmarkRoute
import com.sghore.chimtubeworld.presentation.bookmarkScreen.EditBookmarkRoute
import com.sghore.chimtubeworld.presentation.cafeScreen.CafeRoute
import com.sghore.chimtubeworld.presentation.storeScreen.StoreRoute
import com.sghore.chimtubeworld.presentation.twitchScreen.TwitchRoute
import com.sghore.chimtubeworld.presentation.videosScreen.VideosRoute
import com.sghore.chimtubeworld.presentation.webToonScreen.WebToonRoute
import com.sghore.chimtubeworld.presentation.youtubeScreen.YoutubeRoute
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val gViewModel by viewModels<GlobalViewModel>()
    private var channelNameForToolbar = ""
    private lateinit var navController: NavHostController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ChimtubeWorldTheme(
                darkTheme = isSystemInDarkTheme()
            ) {
                navController = rememberNavController()
                val bottomMenu = listOf(
                    NavigationScreen.Youtube,
                    NavigationScreen.Twitch,
                    NavigationScreen.WebToon,
                    NavigationScreen.Cafe,
                    NavigationScreen.Store
                )

                com.google.accompanist.insets.ui.Scaffold(
                    topBar = {
                        val navBackStackEntry by navController.currentBackStackEntryAsState()
                        // 현재 루트
                        val currentRoute = navBackStackEntry?.destination
                            ?.route
                            ?.substringBefore("/")
                            ?.substringBefore("?")

                        com.google.accompanist.insets.ui.TopAppBar(
                            backgroundColor = colorResource(id = R.color.default_background_color),
                            elevation = 0.dp,
                            modifier = Modifier.fillMaxWidth(),
                            title = {
                                when (currentRoute) {
                                    NavigationScreen.AddBookmark.route, NavigationScreen.EditBookmark.route -> {
                                        Text(
                                            text = "북마크",
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                    NavigationScreen.Videos.route -> {
                                        Text(
                                            text = channelNameForToolbar,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                    else -> {
                                        channelNameForToolbar = "" // 툴바 텍스트 초기화

                                        Text(
                                            text = "CHIMHA",
                                            color = MaterialTheme.colors.onPrimary,
                                            fontSize = 24.sp,
                                            fontWeight = FontWeight.Bold,
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier.fillMaxWidth()
                                        )

                                        gViewModel.bookmarkData?.let {
                                            gViewModel.bookmarkData = null
                                        }
                                    }
                                }
                            },
                            navigationIcon = TopAppBarNavigationItem(currentRoute = currentRoute) {
                                IconButton(onClick = {
                                    navController.navigateUp()
                                }) {
                                    Icon(
                                        imageVector = Icons.Default.ArrowBack,
                                        contentDescription = null
                                    )
                                }
                            },
                            actions = {
                                when (currentRoute) {
                                    NavigationScreen.EditBookmark.route -> {
                                        IconButton(onClick = {
                                            gViewModel.setEventFlow(
                                                event = GlobalViewModel.ActionEvent.CopyVideoUrl
                                            )
                                        }) {
                                            Icon(
                                                imageVector = Icons.Default.Assignment,
                                                contentDescription = null,
                                                tint = MaterialTheme.colors.onPrimary
                                            )
                                        }
                                        IconButton(onClick = {
                                            gViewModel.setEventFlow(
                                                event = GlobalViewModel.ActionEvent.DeleteBookmark
                                            )
                                        }) {
                                            Icon(
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = null,
                                                tint = MaterialTheme.colors.onPrimary
                                            )
                                        }
                                    }
                                    NavigationScreen.Videos.route -> {
                                        IconButton(onClick = {
                                            gViewModel.setEventFlow(
                                                event = GlobalViewModel.ActionEvent.ShowHelp
                                            )
                                        }) {
                                            Icon(
                                                imageVector = Icons.Default.Help,
                                                contentDescription = null,
                                                tint = MaterialTheme.colors.onPrimary
                                            )
                                        }
                                    }
                                    else -> {}
                                }
                            }
                        )
                    },
                    bottomBar = {
                        val navBackStackEntry by navController.currentBackStackEntryAsState()
                        val currentDestination = navBackStackEntry?.destination
                        val currentRoute = currentDestination?.route
                            ?.substringBefore("/")
                            ?.substringBefore("?")

                        BottomNavigationBar(
                            isHide = currentRoute == NavigationScreen.AddBookmark.route || currentRoute == NavigationScreen.EditBookmark.route,
                            navController = navController,
                            bottomMenu = bottomMenu,
                            currentDestination = currentDestination,
                            currentRoute = currentRoute
                        )
                    }
                ) { innerPadding ->
                    Column(modifier = Modifier.padding(innerPadding)) {
                        Divider(
                            color = colorResource(id = R.color.gray_bright_night)
                        )
                        NavHost(
                            navController = navController,
                            startDestination = NavigationScreen.Youtube.route,
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
                                route = NavigationScreen.Videos.route +
                                        "?typeImageRes={typeImageRes}&channelName={channelName}&playlistId={playlistId}&playlistName={playlistName}",
                                arguments = listOf(
                                    navArgument("typeImageRes") { type = NavType.IntType },
                                    navArgument("channelName") { type = NavType.StringType },
                                    navArgument("playlistId") { type = NavType.StringType },
                                    navArgument("playlistName") { type = NavType.StringType }
                                )
                            ) { entry ->
                                channelNameForToolbar = entry.arguments
                                    ?.getString("channelName")
                                    ?: ""

                                VideosRoute(
                                    gViewModel = gViewModel,
                                    navController = navController,
                                    typeImageRes = entry.arguments?.getInt("typeImageRes")
                                        ?: R.drawable.youtube
                                )
                            }
                            composable(
                                route = NavigationScreen.AddBookmark.route + "?url={url}",
                                arguments = listOf(
                                    navArgument("url") { type = NavType.StringType }
                                )
                            ) {
                                AddBookmarkRoute(
                                    gViewModel = gViewModel,
                                    navController = navController
                                )
                            }
                            composable(
                                route = NavigationScreen.EditBookmark.route + "?typeImageRes={typeImageRes}&pos={pos}&video={video}",
                                arguments = listOf(
                                    navArgument("typeImageRes") { type = NavType.IntType },
                                    navArgument("pos") { type = NavType.IntType },
                                    navArgument("video") { type = NavType.StringType }
                                )
                            ) {
                                EditBookmarkRoute(
                                    gViewModel = gViewModel,
                                    navController = navController
                                )
                            }
                        }

                        onNewIntent(intent) // 인텐트로 인해 앱이 실행된것인지 확인
                    }
                }
            }
        }
    }

    // 새로운 인텐트가 들어올 때
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        moveFragment(intent)
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

    // fragment 이동
    private fun moveFragment(intent: Intent?) {
        when (intent?.action) {
            Intent.ACTION_SEND -> {
                // 동영상의 url 주소
                val url = intent.extras?.getString(Intent.EXTRA_TEXT) ?: ""

                if (url.isNotEmpty()) {
                    // 북마크 제작 화면으로 이동
                    val route = NavigationScreen.AddBookmark.route + "?url=${url}"
                    navController.navigate(
                        route = route
                    )
                }
            }
        }
        setIntent(null) // 인텐트 초기화
    }
}