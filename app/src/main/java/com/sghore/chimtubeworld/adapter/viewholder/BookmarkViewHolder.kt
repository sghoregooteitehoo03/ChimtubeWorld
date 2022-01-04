package com.sghore.chimtubeworld.adapter.viewholder

import androidx.recyclerview.widget.RecyclerView
import com.sghore.chimtubeworld.adapter.BookmarkAdapter
import com.sghore.chimtubeworld.data.Bookmark
import com.sghore.chimtubeworld.databinding.ItemBookmarkBinding

class BookmarkViewHolder(
    private val binding: ItemBookmarkBinding,
    private val itemListener: BookmarkAdapter.BookmarkItemListener
) : RecyclerView.ViewHolder(binding.root) {

    init {
        itemView.setOnClickListener {
            itemListener.onBookmarkClickListener(bindingAdapterPosition)
        }
    }

    fun bind(data: Bookmark) {
        binding.bookmarkData = data
    }
}