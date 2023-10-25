package com.sghore.chimtubeworld.presentation.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.viewpager2.widget.ViewPager2
import com.sghore.chimtubeworld.R
import com.sghore.chimtubeworld.data.model.Goods
import com.sghore.chimtubeworld.databinding.ActivityViewPagerBinding
import com.sghore.chimtubeworld.other.Contents
import com.sghore.chimtubeworld.presentation.ui.adapter.StoreDetailPagerAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ViewPagerActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var pagerAdapter: StoreDetailPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 인스턴스 설정
        val binding = DataBindingUtil.setContentView<ActivityViewPagerBinding?>(
            this,
            R.layout.activity_view_pager
        ).apply {
            this.clickListener = this@ViewPagerActivity

            lifecycleOwner = this@ViewPagerActivity
        }

        val bundle = intent.extras

        // 굿즈 리스트
        val goodsList = bundle?.getParcelableArray(Contents.KEY_GOODS_LIST)
            ?.toList()
        // 선택된 굿즈 위치
        val selectedPos = bundle?.getInt(Contents.KEY_SELECTED_POS, 0) ?: 0

        if (goodsList == null) {
            finish()
        } else {
            pagerAdapter = StoreDetailPagerAdapter(
                supportFragmentManager,
                lifecycle,
                goodsList.map { it as Goods }
            )

            setViewPager(binding.goodsPager, selectedPos)
            intent.removeExtra(Contents.KEY_GOODS_LIST) // 데이터 포맷
        }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(0, 0)
    }

    // 뷰페이저 설정
    private fun setViewPager(viewPager: ViewPager2, selectedPos: Int) {
        val pageMarginPx = resources.getDimensionPixelOffset(R.dimen.pageMargin)
        val pagerWidth = resources.getDimensionPixelOffset(R.dimen.pagerWidth)
        val screenWidth = resources.displayMetrics.widthPixels
        val offsetPx = screenWidth - pageMarginPx - pagerWidth

        with(viewPager) {
            this.adapter = pagerAdapter
            this.offscreenPageLimit = 3
            this.setPageTransformer { page, position ->
                page.translationX = position * -offsetPx
                page.translationZ = position * -pageMarginPx
            }
            this.setCurrentItem(selectedPos, false)
        }
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.close_pager_btn -> {
                finish()
            }

            else -> {}
        }
    }
}