package com.sghore.chimtubeworld.presentation.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.plcoding.cryptocurrencyappyt.presentation.ui.theme.ChimtubeWorldTheme
import com.sghore.chimtubeworld.R
import com.sghore.chimtubeworld.presentation.cafeScreen.CafeRoute
import com.sghore.chimtubeworld.presentation.storeScreen.StoreRoute
import com.sghore.chimtubeworld.presentation.twitchScreen.TwitchRoute
import com.sghore.chimtubeworld.presentation.webToonScreen.WebToonRoute
import com.sghore.chimtubeworld.presentation.youtubeScreen.YoutubeRoute
import dagger.hilt.android.AndroidEntryPoint

// TODO:
//  . StoreDetailFragment 재구성 버그 수정 ㅁ

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
                    BottomNavigationScreen.Youtube,
                    BottomNavigationScreen.Twitch,
                    BottomNavigationScreen.WebToon,
                    BottomNavigationScreen.Cafe,
                    BottomNavigationScreen.Store
                )

                Scaffold(
                    topBar = {
                        TopAppBar(
                            elevation = 0.dp
                        ) {
                            Column {
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

                            bottomMenu.forEach { menu ->
                                val isSelected =
                                    currentDestination?.hierarchy?.any { it.route == menu.route } == true

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
                            startDestination = BottomNavigationScreen.Youtube.route,
                            modifier = Modifier.padding(innerPadding)
                        ) {
                            composable(route = BottomNavigationScreen.Youtube.route) {
                                YoutubeRoute(navController = navController)
                            }
                            composable(route = BottomNavigationScreen.Twitch.route) {
                                TwitchRoute(navController = navController)
                            }
                            composable(route = BottomNavigationScreen.WebToon.route) {
                                WebToonRoute()
                            }
                            composable(route = BottomNavigationScreen.Cafe.route) {
                                CafeRoute()
                            }
                            composable(route = BottomNavigationScreen.Store.route) {
                                StoreRoute(gViewModel = gViewModel)
                            }
                        }
                    }
                }
            }
        }
    }

    //    private val globalViewModel by viewModels<GlobalViewModel>()
//
//    private lateinit var navController: NavController
//    private lateinit var binding: ActivityMainBinding
//    private lateinit var pagerAdapter: StoreDetailPagerAdapter
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        // 인스턴스 설정
//        binding = DataBindingUtil.setContentView<ActivityMainBinding?>(this, R.layout.activity_main)
//            .apply {
//                this.gviewmodel = globalViewModel
//
//                lifecycleOwner = this@MainActivity
//            }
//        pagerAdapter = StoreDetailPagerAdapter(
//            supportFragmentManager,
//            lifecycle
//        )
//
//        // 툴바 설정
//        setSupportActionBar(binding.mainToolbar)
//
//        // Bottom Nav 설정
//        val navFrag =
//            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
//        navController = navFrag.navController
//        navController.let {
//            binding.bottomNavView.itemIconTintList = null
//            binding.bottomNavView.setupWithNavController(it)
//
//            it.addOnDestinationChangedListener { controller, destination, arguments ->
//                when (destination.id) {
//                    R.id.videosFragment -> { // 영상 리스트 화면
//                        // 아이콘이 선택되게 함
//                        if (controller.backQueue.size == 3) { // YoutubeFrag -> VideoFrag
//                            setMenuItemChecked(false, binding.bottomNavView.selectedItemId)
//                            setMenuItemChecked(true, R.id.youtubeFragment)
//                        } else if (controller.backQueue.size == 4) { // TwtichFrag -> VideoFrag
//                            setMenuItemChecked(false, binding.bottomNavView.selectedItemId)
//                            setMenuItemChecked(true, R.id.twitchFragment)
//                        }
//
//                        binding.toolbarText.visibility = View.GONE
//                        binding.bottomNavView.visibility = View.VISIBLE
//                        supportActionBar?.setDisplayHomeAsUpEnabled(true)
//                        supportActionBar?.title = ""
//                    }
//                    R.id.addBookmarkFragment, R.id.editBookmarkFragment -> { // 북마크 추가 화면
//                        binding.toolbarText.visibility = View.GONE
//                        binding.bottomNavView.visibility = View.GONE
//                        supportActionBar?.setDisplayHomeAsUpEnabled(true)
//                        supportActionBar?.title = "북마크"
//                    }
//                    else -> {
//                        binding.toolbarText.visibility = View.VISIBLE
//                        binding.bottomNavView.visibility = View.VISIBLE
//                        supportActionBar?.setDisplayHomeAsUpEnabled(false)
//                        supportActionBar?.title = ""
//
//                        if (globalViewModel.bookmarkData.value != null) {
//                            globalViewModel.bookmarkData.value = null
//                        }
//                    }
//                }
//            }
//        }
//
//        binding.closePagerBtn.setOnClickListener {
//            onBackPressed()
//        }
//
//        setViewPager()
//        setObserver()
//        onNewIntent(intent) // 인텐트로 인해 앱이 실행된것인지 확인
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        return when (item.itemId) {
//            android.R.id.home -> {
//                onBackPressed()
//                true
//            }
//            else -> false
//        }
//    }
//
//    override fun onBackPressed() {
//        // 페이저 레이아웃이 보이고 있을 때
//        if (globalViewModel.showGoodsList.value != null) {
//            // 레이아웃에서 벗어남
//            clearGoodsData()
//        } else {
//            super.onBackPressed()
//        }
//    }
//
//    // 새로운 인텐트가 들어올 때
//    override fun onNewIntent(intent: Intent?) {
//        super.onNewIntent(intent)
//        moveFragment(intent)
//    }
//
//    // 옵저버 설정
//    private fun setObserver() {
//        // 굿즈 리스트
//        globalViewModel.showGoodsList.observe(this) { goodsList ->
//            if (goodsList != null) {
//                setStatusBarColor(Color.BLACK)
//                pagerAdapter.syncData(goodsList)
//            } else {
//                if (isDarkMode()) {
//                    setStatusBarColor(Color.BLACK)
//                } else {
//                    setStatusBarColor(Color.WHITE)
//                }
//
//                pagerAdapter.notifyChangeInPosition(1)
//                pagerAdapter.syncData(listOf(null))
//            }
//        }
//        // 아이템이 선택된 위치
//        globalViewModel.selectedGoodsPos.observe(this) { pos ->
//            if (pos != -1) {
//                binding.detailViewpager.setCurrentItem(pos, false)
//            }
//        }
//    }
//
//    // 뷰페이저 설정
//    private fun setViewPager() {
//        val pageMarginPx = resources.getDimensionPixelOffset(R.dimen.pageMargin)
//        val pagerWidth = resources.getDimensionPixelOffset(R.dimen.pagerWidth)
//        val screenWidth = resources.displayMetrics.widthPixels
//        val offsetPx = screenWidth - pageMarginPx - pagerWidth
//
//        // 값 초기화
//        clearGoodsData()
//
//        binding.detailViewpager.adapter = pagerAdapter
//        binding.detailViewpager.offscreenPageLimit = 3
//        binding.detailViewpager.setPageTransformer { page, position ->
//            page.translationX = position * -offsetPx
//            page.translationZ = position * -pageMarginPx
//        }
//    }
//
//    private fun setMenuItemChecked(isChecked: Boolean, fragmentRes: Int) {
//        binding.bottomNavView
//            .menu
//            .findItem(fragmentRes)
//            .isChecked = isChecked
//    }
//
//    private fun isDarkMode(): Boolean {
//        return when (resources.configuration.uiMode.and(Configuration.UI_MODE_NIGHT_MASK)) {
//            Configuration.UI_MODE_NIGHT_YES -> true
//            Configuration.UI_MODE_NIGHT_NO -> false
//            else -> false
//        }
//    }
//
//    // Status bar 설정
//    private fun setStatusBarColor(color: Int) {
//        if (color == Color.WHITE) {
//            with(window) {
//                decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
//                statusBarColor = Color.WHITE
//            }
//        } else {
//            with(window) {
//                decorView.systemUiVisibility = 0
//                statusBarColor = Color.BLACK
//            }
//        }
//    }
//
//    // fragment 이동
//    private fun moveFragment(intent: Intent?) {
//        when (intent?.action) {
//            Intent.ACTION_SEND -> {
//                // 동영상의 url 주소
//                val url = intent.extras?.getString(Intent.EXTRA_TEXT) ?: ""
//
//                if (url.isNotEmpty()) {
//                    val direction =
//                        AddBookmarkFragmentDirections.actionGlobalAddBookmarkFragment(url)
//
//                    // 북마크 제작 화면으로 이동
//                    navController.navigate(direction)
//                }
//            }
//        }
//        setIntent(null) // 인텐트 초기화
//    }
//
//    private fun clearGoodsData() {
//        globalViewModel.showGoodsList.value = null
//        globalViewModel.selectedGoodsPos.value = -1
//    }
}