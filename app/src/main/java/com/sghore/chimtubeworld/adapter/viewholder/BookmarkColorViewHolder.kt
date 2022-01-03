package com.sghore.chimtubeworld.adapter.viewholder

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.sghore.chimtubeworld.R
import com.sghore.chimtubeworld.adapter.BookmarkColorAdapter
import com.sghore.chimtubeworld.databinding.ItemBookmarkColorBinding

class BookmarkColorViewHolder(
    private val binding: ItemBookmarkColorBinding,
    private val itemListener: BookmarkColorAdapter.BookmarkColorItemListener
) : RecyclerView.ViewHolder(binding.root) {

    init {
        itemView.setOnClickListener {
            itemListener.onItemClickListener(bindingAdapterPosition)
        }
    }

    fun bind(data: Int, isSelected: Boolean) {
        binding.colorData = data

        itemView.foreground = if (isSelected) {
            itemView.resources.getDrawable(R.color.black_alpha_30, null)
        } else {
            itemView.resources.getDrawable(android.R.color.transparent, null)
        }
    }
}