package com.sghore.chimtubeworld.adapter.viewholder

import androidx.recyclerview.widget.RecyclerView
import com.sghore.chimtubeworld.data.Channel
import com.sghore.chimtubeworld.databinding.ItemWebtoonBinding

class WebToonViewHolder(
    val binding: ItemWebtoonBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(data: Channel) {
        binding.channelData = data
    }
}