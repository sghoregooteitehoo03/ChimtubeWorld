package com.sghore.chimtubeworld.adapter.viewholder

import androidx.recyclerview.widget.RecyclerView
import com.sghore.chimtubeworld.adapter.GoodsAdapter
import com.sghore.chimtubeworld.data.model.Goods
import com.sghore.chimtubeworld.databinding.ItemGoodsInfoBinding


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