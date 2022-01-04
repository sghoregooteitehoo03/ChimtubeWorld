package com.sghore.chimtubeworld.adapter.viewholder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.sghore.chimtubeworld.adapter.BookmarkAdapter
import com.sghore.chimtubeworld.adapter.VideosPagingAdapter
import com.sghore.chimtubeworld.data.Video
import com.sghore.chimtubeworld.databinding.ItemVideoBinding
import com.sghore.chimtubeworld.ui.custom.LinearItemDecoration

class VideosViewHolder(
    private val binding: ItemVideoBinding,
    private val itemListener: VideosPagingAdapter.VideosItemListener
) : RecyclerView.ViewHolder(binding.root) {

    init {
        itemView.setOnClickListener {
            itemListener.onVideoClickListener(bindingAdapterPosition)
        }
    }

    fun bind(data: Video?, adapter: BookmarkAdapter) {
        binding.videoData = data
        with(binding.bookmarkList) {
            if (adapter.itemCount != 0) {
                this.adapter = adapter
                this.visibility = View.VISIBLE
            } else {
                this.adapter = null
                this.visibility = View.GONE
            }
        }
    }
}