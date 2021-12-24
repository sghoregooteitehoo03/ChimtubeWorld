package com.sghore.chimtubeworld.adapter.viewholder

import android.content.res.Resources
import android.util.DisplayMetrics
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sghore.chimtubeworld.adapter.GoodsAdapter
import com.sghore.chimtubeworld.data.Goods
import com.sghore.chimtubeworld.data.Post
import com.sghore.chimtubeworld.databinding.ItemGoodsInfoBinding
import kotlin.math.roundToInt


class GoodsViewHolder(
    private val binding: ItemGoodsInfoBinding,
    private val itemListener: GoodsAdapter.GoodsItemListener
) : RecyclerView.ViewHolder(binding.root) {

    init {
        itemView.setOnClickListener {
            itemListener.onGoodsClickListener(bindingAdapterPosition)
        }
    }

    fun bind(data: Goods) {
        binding.goodsData = data
    }
}