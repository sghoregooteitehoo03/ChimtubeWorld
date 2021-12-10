package com.sghore.chimtubeworld.adapter.viewholder

import androidx.recyclerview.widget.RecyclerView
import com.sghore.chimtubeworld.databinding.ItemCafeCategoryBinding

class CafeCategoryViewHolder(
    private val binding: ItemCafeCategoryBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(data: String) {
        binding.categoryData = data
    }
}