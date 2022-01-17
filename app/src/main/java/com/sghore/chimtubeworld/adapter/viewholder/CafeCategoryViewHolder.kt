package com.sghore.chimtubeworld.adapter.viewholder

import androidx.recyclerview.widget.RecyclerView
import com.sghore.chimtubeworld.adapter.CafeCategoryAdapter
import com.sghore.chimtubeworld.data.model.CafeCategory
import com.sghore.chimtubeworld.databinding.ItemCafeCategoryBinding

class CafeCategoryViewHolder(
    private val binding: ItemCafeCategoryBinding,
    private val itemListener: CafeCategoryAdapter.CafeCategoryItemListener
) : RecyclerView.ViewHolder(binding.root) {

    init {
        itemView.setOnClickListener {
            itemListener.onCategoryClickListener(bindingAdapterPosition)
        }
    }

    fun bind(data: CafeCategory) {
        binding.categoryData = data
    }
}