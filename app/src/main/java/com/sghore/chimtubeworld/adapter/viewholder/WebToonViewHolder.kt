package com.sghore.chimtubeworld.adapter.viewholder

import androidx.recyclerview.widget.RecyclerView
import com.sghore.chimtubeworld.adapter.WebToonAdapter
import com.sghore.chimtubeworld.data.Channel
import com.sghore.chimtubeworld.databinding.ItemWebtoonBinding

class WebToonViewHolder(
    private val binding: ItemWebtoonBinding,
    private val itemListener: WebToonAdapter.WebToonItemListener
) : RecyclerView.ViewHolder(binding.root) {

    init {
        itemView.setOnClickListener {
            itemListener.onItemClickListener(bindingAdapterPosition)
        }
    }

    fun bind(data: Channel) {
        binding.channelData = data
    }
}