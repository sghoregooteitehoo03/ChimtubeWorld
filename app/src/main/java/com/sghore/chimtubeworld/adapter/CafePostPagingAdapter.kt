package com.sghore.chimtubeworld.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.sghore.chimtubeworld.adapter.viewholder.CafePostViewHolder
import com.sghore.chimtubeworld.data.Post
import com.sghore.chimtubeworld.databinding.ItemCafePostBinding

class CafePostPagingAdapter : PagingDataAdapter<Post, CafePostViewHolder>(diffUtil) {

    override fun onBindViewHolder(holder: CafePostViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CafePostViewHolder {
        val view =
            ItemCafePostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CafePostViewHolder(view)
    }

    private companion object diffUtil : DiffUtil.ItemCallback<Post>() {
        override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
            return oldItem.title == newItem.title
        }

        override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
            return oldItem == newItem
        }
    }
}