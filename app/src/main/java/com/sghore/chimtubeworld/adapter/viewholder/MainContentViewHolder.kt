package com.sghore.chimtubeworld.adapter.viewholder

import androidx.recyclerview.widget.RecyclerView
import com.sghore.chimtubeworld.adapter.MainContentAdapter
import com.sghore.chimtubeworld.data.Channel
import com.sghore.chimtubeworld.databinding.ItemContentMainBinding

class MainContentViewHolder(
    private val binding: ItemContentMainBinding,
    private val listener: MainContentAdapter.MainContentItemListener
) : RecyclerView.ViewHolder(binding.root) {

    init {
        itemView.setOnClickListener {
            listener.onMainItemClickListener(bindingAdapterPosition)
        }
    }

    fun bind(data: Channel?) {
        binding.channelData = data
    }
}