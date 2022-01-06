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
    private val bookmarkAdapter = BookmarkAdapter().apply {
        setOnItemListener(object : BookmarkAdapter.BookmarkItemListener {
            override fun onBookmarkClickListener(pos: Int) {
                itemListener.onBookmarkClickListener(
                    videoPos = bindingAdapterPosition,
                    bookmarkPos = pos
                )
            }
        })
    }

    init {
        itemView.setOnClickListener {
            itemListener.onVideoClickListener(bindingAdapterPosition)
        }
    }

    fun bind(data: Video?) {
        binding.videoData = data
        with(binding.bookmarkList) {
            if (data?.bookmarks?.size != 0) {
                this.adapter = bookmarkAdapter.apply {
                    syncData(data?.bookmarks ?: listOf())
                }
                if (this.itemDecorationCount == 0) { // 한번만 추가되게
                    this.addItemDecoration(LinearItemDecoration(itemView.context, 8))
                }
                this.visibility = View.VISIBLE
            } else {
                this.adapter = null
                this.visibility = View.GONE
            }
        }
    }
}