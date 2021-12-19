package com.sghore.chimtubeworld.adapter.viewholder

import androidx.recyclerview.widget.RecyclerView
import com.sghore.chimtubeworld.adapter.TwitchUserAdapter
import com.sghore.chimtubeworld.data.Channel
import com.sghore.chimtubeworld.databinding.ItemCrewBinding

class TwitchUserViewHolder(
    private val binding: ItemCrewBinding,
    private val itemListener: TwitchUserAdapter.TwitchUserItemListener
) : RecyclerView.ViewHolder(binding.root) {

    init {
        itemView.setOnClickListener {
            itemListener.onItemClickListener(bindingAdapterPosition)
        }
    }

    fun bind(data: Channel?) {
        binding.channelData = data
    }
}