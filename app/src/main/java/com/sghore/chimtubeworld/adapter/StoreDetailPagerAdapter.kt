package com.sghore.chimtubeworld.adapter

import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.sghore.chimtubeworld.data.Goods
import com.sghore.chimtubeworld.ui.fragment.StoreDetailFragment

class StoreDetailPagerAdapter(
    fm: FragmentManager,
    lifecycle: Lifecycle
) : FragmentStateAdapter(fm, lifecycle) {
    private var goodsList = listOf<Goods?>()
    private var baseId: Long = 0

    override fun getItemCount() =
        goodsList.size

    override fun createFragment(position: Int) =
        StoreDetailFragment(goodsList[position])

    override fun getItemViewType(position: Int): Int {
        return PagerAdapter.POSITION_NONE
    }

    override fun getItemId(position: Int): Long {
        // give an ID different from position when position has been changed
        return baseId + position
    }

    fun notifyChangeInPosition(n: Int) {
        // shift the ID returned by getItemId outside the range of all previous fragments
        baseId += itemCount + n
    }

    fun syncData(_goodsList: List<Goods?>) {
        goodsList = _goodsList
        notifyDataSetChanged()
    }
}