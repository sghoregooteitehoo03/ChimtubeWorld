package com.sghore.chimtubeworld.adapter.viewholder

import androidx.recyclerview.widget.RecyclerView
import com.sghore.chimtubeworld.adapter.GoodsCategoryAdapter
import com.sghore.chimtubeworld.data.Channel
import com.sghore.chimtubeworld.databinding.ItemGoodsCategoryBinding

class GoodsCategoryViewHolder(
    private val binding: ItemGoodsCategoryBinding,
    private val itemListener: GoodsCategoryAdapter.GoodsCategoryItemListener
) : RecyclerView.ViewHolder(binding.root) {

    init {
        itemView.setOnClickListener {
            itemListener.onCategoryClickListener(bindingAdapterPosition)
        }
    }

    fun bind(data: Channel) {
        binding.channelData = data
    }
}