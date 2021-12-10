package com.sghore.chimtubeworld.adapter.viewholder

import androidx.recyclerview.widget.RecyclerView
import com.sghore.chimtubeworld.data.Post
import com.sghore.chimtubeworld.databinding.ItemCafePostBinding

class CafePostViewHolder(
    private val binding: ItemCafePostBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(data: Post?) {
        binding.postData = data
    }
}