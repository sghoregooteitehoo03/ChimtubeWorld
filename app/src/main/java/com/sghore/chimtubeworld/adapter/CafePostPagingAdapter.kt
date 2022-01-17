package com.sghore.chimtubeworld.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.sghore.chimtubeworld.adapter.viewholder.CafePostViewHolder
import com.sghore.chimtubeworld.data.model.Post
import com.sghore.chimtubeworld.databinding.ItemCafePostBinding

class CafePostPagingAdapter : PagingDataAdapter<Post, CafePostViewHolder>(diffUtil) {
    private lateinit var mListener: CafePostItemListener

    interface CafePostItemListener {
        fun onPostClickListener(pos: Int)
    }

    override fun onBindViewHolder(holder: CafePostViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CafePostViewHolder {
        val view =
            ItemCafePostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CafePostViewHolder(view, mListener)
    }

    private companion object diffUtil : DiffUtil.ItemCallback<Post>() {
        override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
            return oldItem.url == newItem.url
        }

        override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
            return oldItem == newItem
        }
    }

    fun setOnItemListener(_listener: CafePostItemListener) {
        mListener = _listener
    }

    fun getPostData(pos: Int) =
        getItem(pos)
}