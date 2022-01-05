package com.sghore.chimtubeworld.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.sghore.chimtubeworld.adapter.viewholder.VideosViewHolder
import com.sghore.chimtubeworld.data.Bookmark
import com.sghore.chimtubeworld.data.Video
import com.sghore.chimtubeworld.databinding.ItemVideoBinding

class VideosPagingAdapter : PagingDataAdapter<Video, VideosViewHolder>(diffUtil) {
    lateinit var mListener: VideosItemListener

    interface VideosItemListener {
        fun onVideoClickListener(pos: Int)
        fun onBookmarkClickListener(videoPos: Int, bookmarkPos: Int)
    }

    override fun onBindViewHolder(holder: VideosViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideosViewHolder {
        val view =
            ItemVideoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VideosViewHolder(view, mListener)
    }

    private companion object diffUtil : DiffUtil.ItemCallback<Video>() {

        override fun areItemsTheSame(oldItem: Video, newItem: Video): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Video, newItem: Video): Boolean {
            return oldItem == newItem
        }
    }

    fun setOnItemListener(_listener: VideosItemListener) {
        mListener = _listener
    }

    fun getVideoData(pos: Int) =
        getItem(pos)
}