package com.sghore.chimtubeworld.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import androidx.viewpager2.widget.ViewPager2
import com.sghore.chimtubeworld.R
import com.sghore.chimtubeworld.adapter.StoreDetailPagerAdapter
import com.sghore.chimtubeworld.databinding.ActivityMainBinding
import com.sghore.chimtubeworld.viewmodel.GlobalViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val globalViewModel by viewModels<GlobalViewModel>()

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

        // Bottom Nav 설정
        val navFrag =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navFrag.navController

        binding.bottomNavView.setupWithNavController(navController)
        setViewPager()
        setObserver()
    }

    // 옵저버 설정
    private fun setObserver() {
        // 굿즈 리스트
        globalViewModel.showGoodsList.observe(this) { goodsList ->
            if (goodsList != null) {
                pagerAdapter.syncData(goodsList)
            } else {
                pagerAdapter.notifyChangeInPosition(1)
            }
        }
        // 아이템이 선택된 위치
        globalViewModel.selectedGoodsPos.observe(this) { pos ->
            binding.detailViewpager.setCurrentItem(pos, false)
        }
    }

    // 뷰페이저 설정
    private fun setViewPager() {
        val pageMarginPx = resources.getDimensionPixelOffset(R.dimen.pageMargin)
        val pagerWidth = resources.getDimensionPixelOffset(R.dimen.pagerWidth)
        val screenWidth = resources.displayMetrics.widthPixels
        val offsetPx = screenWidth - pageMarginPx - pagerWidth

        binding.detailViewpager.adapter = pagerAdapter
        binding.detailViewpager.offscreenPageLimit = 3
        binding.detailViewpager.setPageTransformer { page, position ->
            page.translationX = position * -offsetPx
            page.translationZ = position * -pageMarginPx
        }
    }

    override fun onBackPressed() {
        // 페이저 레이아웃이 보이고 있을 때
        if (globalViewModel.showGoodsList.value != null) {
            // 레이아웃에서 벗어남
            globalViewModel.showGoodsList.value = null
            globalViewModel.selectedGoodsPos.value = -1
            pagerAdapter.syncData(listOf())
        } else {
            super.onBackPressed()
        }
    }
}