package com.sghore.chimtubeworld.presentation.ui.adapter

import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.sghore.chimtubeworld.data.model.Goods
import com.sghore.chimtubeworld.presentation.storeDetailScreen.StoreDetailFragment

class StoreDetailPagerAdapter(
    fm: FragmentManager,
    lifecycle: Lifecycle,
    private val goodsList: List<Goods>
) : FragmentStateAdapter(fm, lifecycle) {

    override fun getItemCount() =
        goodsList.size

    override fun createFragment(position: Int) =
        StoreDetailFragment(goodsList[position])
}