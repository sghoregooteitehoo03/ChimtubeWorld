package com.sghore.chimtubeworld.adapter.viewholder

import androidx.recyclerview.widget.RecyclerView
import com.sghore.chimtubeworld.data.Channel
import com.sghore.chimtubeworld.databinding.ItemContentSubBinding

class SubContentViewHolder(
    private val binding: ItemContentSubBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(data: Channel) {
        binding.channelData = data
    }
}