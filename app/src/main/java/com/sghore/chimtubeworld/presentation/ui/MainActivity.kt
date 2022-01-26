package com.sghore.chimtubeworld.presentation.ui

import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.sghore.chimtubeworld.R
import com.sghore.chimtubeworld.presentation.ui.adapter.StoreDetailPagerAdapter
import com.sghore.chimtubeworld.databinding.ActivityMainBinding
import com.sghore.chimtubeworld.presentation.bookmarkScreen.AddBookmarkFragmentDirections
import dagger.hilt.android.AndroidEntryPoint

// TODO:
//  . StoreDetailFragment 재구성 버그 수정 ㅁ
//  . Compose 부분적 마이그레이션 O
//  . 다크모드 clickable 애니메이션 안보이는 버그 수정 O
//  . state 형식 변경 O
//  . repository 기능별 분리 O
//  . 쓸데없는 재구성 지우기 O
//  . 앱 아이콘, 아이콘, 스플래쉬 화면 이미지 변경
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val globalViewModel by viewModels<GlobalViewModel>()

    private lateinit var navController: NavController
    private lateinit var binding: ActivityMainBinding
    private lateinit var pagerAdapter: StoreDetailPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 인스턴스 설정
        binding = DataBindingUtil.setContentView<ActivityMainBinding?>(this, R.layout.activity_main)
            .apply {
                this.gviewmodel = globalViewModel

                lifecycleOwner = this@MainActivity
            }
        pagerAdapter = StoreDetailPagerAdapter(
            supportFragmentManager,
            lifecycle
        )

        // 툴바 설정
        setSupportActionBar(binding.mainToolbar)

        // Bottom Nav 설정
        val navFrag =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navFrag.navController
        navController.let {
            binding.bottomNavView.itemIconTintList = null
            binding.bottomNavView.setupWithNavController(it)

            it.addOnDestinationChangedListener { controller, destination, arguments ->
                when (destination.id) {
                    R.id.videosFragment -> { // 영상 리스트 화면
                        // 아이콘이 선택되게 함
                        if (controller.backQueue.size == 3) { // YoutubeFrag -> VideoFrag
                            setMenuItemChecked(false, binding.bottomNavView.selectedItemId)
                            setMenuItemChecked(true, R.id.youtubeFragment)
                        } else if (controller.backQueue.size == 4) { // TwtichFrag -> VideoFrag
                            setMenuItemChecked(false, binding.bottomNavView.selectedItemId)
                            setMenuItemChecked(true, R.id.twitchFragment)
                        }

                        binding.toolbarText.visibility = View.GONE
                        binding.bottomNavView.visibility = View.VISIBLE
                        supportActionBar?.setDisplayHomeAsUpEnabled(true)
                        supportActionBar?.title = ""
                    }
                    R.id.addBookmarkFragment, R.id.editBookmarkFragment -> { // 북마크 추가 화면
                        binding.toolbarText.visibility = View.GONE
                        binding.bottomNavView.visibility = View.GONE
                        supportActionBar?.setDisplayHomeAsUpEnabled(true)
                        supportActionBar?.title = "북마크"
                    }
                    else -> {
                        binding.toolbarText.visibility = View.VISIBLE
                        binding.bottomNavView.visibility = View.VISIBLE
                        supportActionBar?.setDisplayHomeAsUpEnabled(false)
                        supportActionBar?.title = ""

                        if (globalViewModel.bookmarkData.value != null) {
                            globalViewModel.bookmarkData.value = null
                        }
                    }
                }
            }
        }

        binding.closePagerBtn.setOnClickListener {
            onBackPressed()
        }

        setViewPager()
        setObserver()
        onNewIntent(intent) // 인텐트로 인해 앱이 실행된것인지 확인
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> false
        }
    }

    override fun onBackPressed() {
        // 페이저 레이아웃이 보이고 있을 때
        if (globalViewModel.showGoodsList.value != null) {
            // 레이아웃에서 벗어남
            clearGoodsData()
        } else {
            super.onBackPressed()
        }
    }

    // 새로운 인텐트가 들어올 때
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        moveFragment(intent)
    }

    // 옵저버 설정
    private fun setObserver() {
        // 굿즈 리스트
        globalViewModel.showGoodsList.observe(this) { goodsList ->
            if (goodsList != null) {
                setStatusBarColor(Color.BLACK)
                pagerAdapter.syncData(goodsList)
            } else {
                if (isDarkMode()) {
                    setStatusBarColor(Color.BLACK)
                } else {
                    setStatusBarColor(Color.WHITE)
                }

                pagerAdapter.notifyChangeInPosition(1)
                pagerAdapter.syncData(listOf(null))
            }
        }
        // 아이템이 선택된 위치
        globalViewModel.selectedGoodsPos.observe(this) { pos ->
            if (pos != -1) {
                binding.detailViewpager.setCurrentItem(pos, false)
            }
        }
    }

    // 뷰페이저 설정
    private fun setViewPager() {
        val pageMarginPx = resources.getDimensionPixelOffset(R.dimen.pageMargin)
        val pagerWidth = resources.getDimensionPixelOffset(R.dimen.pagerWidth)
        val screenWidth = resources.displayMetrics.widthPixels
        val offsetPx = screenWidth - pageMarginPx - pagerWidth

        // 값 초기화
        clearGoodsData()

        binding.detailViewpager.adapter = pagerAdapter
        binding.detailViewpager.offscreenPageLimit = 3
        binding.detailViewpager.setPageTransformer { page, position ->
            page.translationX = position * -offsetPx
            page.translationZ = position * -pageMarginPx
        }
    }

    private fun setMenuItemChecked(isChecked: Boolean, fragmentRes: Int) {
        binding.bottomNavView
            .menu
            .findItem(fragmentRes)
            .isChecked = isChecked
    }

    private fun isDarkMode(): Boolean {
        return when (resources.configuration.uiMode.and(Configuration.UI_MODE_NIGHT_MASK)) {
            Configuration.UI_MODE_NIGHT_YES -> true
            Configuration.UI_MODE_NIGHT_NO -> false
            else -> false
        }
    }

    // Status bar 설정
    private fun setStatusBarColor(color: Int) {
        if (color == Color.WHITE) {
            with(window) {
                decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                statusBarColor = Color.WHITE
            }
        } else {
            with(window) {
                decorView.systemUiVisibility = 0
                statusBarColor = Color.BLACK
            }
        }
    }

    // fragment 이동
    private fun moveFragment(intent: Intent?) {
        when (intent?.action) {
            Intent.ACTION_SEND -> {
                // 동영상의 url 주소
                val url = intent.extras?.getString(Intent.EXTRA_TEXT) ?: ""

                if (url.isNotEmpty()) {
                    val direction =
                        AddBookmarkFragmentDirections.actionGlobalAddBookmarkFragment(url)

                    // 북마크 제작 화면으로 이동
                    navController.navigate(direction)
                }
            }
        }
        setIntent(null) // 인텐트 초기화
    }

    private fun clearGoodsData() {
        globalViewModel.showGoodsList.value = null
        globalViewModel.selectedGoodsPos.value = -1
    }
}