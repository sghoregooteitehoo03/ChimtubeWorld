package com.sghore.chimtubeworld.adapter.viewholder

import androidx.recyclerview.widget.RecyclerView
import com.sghore.chimtubeworld.adapter.VideosPagingAdapter
import com.sghore.chimtubeworld.data.Video
import com.sghore.chimtubeworld.databinding.ItemVideoBinding

class VideosViewHolder(
    private val binding: ItemVideoBinding,
    private val itemListener: VideosPagingAdapter.VideosItemListener
) : RecyclerView.ViewHolder(binding.root) {

    init {
        itemView.setOnClickListener {
            itemListener.onVideoClickListener(bindingAdapterPosition)
        }
    }

    fun bind(data: Video?) {
        binding.videoData = data
    }
}