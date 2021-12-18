package com.sghore.chimtubeworld.adapter.viewholder

import androidx.recyclerview.widget.RecyclerView
import com.sghore.chimtubeworld.adapter.SubContentAdapter
import com.sghore.chimtubeworld.data.Channel
import com.sghore.chimtubeworld.databinding.ItemContentSubBinding

class SubContentViewHolder(
    private val binding: ItemContentSubBinding,
    private val itemListener: SubContentAdapter.SubContentItemListener
) : RecyclerView.ViewHolder(binding.root) {

    init {
        itemView.setOnClickListener {
            itemListener.onSubItemClickListener(bindingAdapterPosition)
        }
    }

    fun bind(data: Channel?) {
        binding.channelData = data
    }
}