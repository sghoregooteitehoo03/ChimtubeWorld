package com.sghore.chimtubeworld.adapter.viewholder

import androidx.recyclerview.widget.RecyclerView
import com.sghore.chimtubeworld.adapter.CafePostPagingAdapter
import com.sghore.chimtubeworld.data.Post
import com.sghore.chimtubeworld.databinding.ItemCafePostBinding

class CafePostViewHolder(
    private val binding: ItemCafePostBinding,
    private val itemListener: CafePostPagingAdapter.CafePostItemListener
) : RecyclerView.ViewHolder(binding.root) {

    init {
        itemView.setOnClickListener {
            itemListener.onPostClickListener(bindingAdapterPosition)
        }
    }

    fun bind(data: Post?) {
        binding.postData = data
    }
}