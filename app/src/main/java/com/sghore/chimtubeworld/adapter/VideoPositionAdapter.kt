package com.sghore.chimtubeworld.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sghore.chimtubeworld.adapter.viewholder.VideoPositionViewHolder
import com.sghore.chimtubeworld.data.model.Bookmark
import com.sghore.chimtubeworld.databinding.ItemVideoPositionBinding

class VideoPositionAdapter(
    private val bookmarks: MutableList<Bookmark?>
) : RecyclerView.Adapter<VideoPositionViewHolder>() {
    private lateinit var itemListener: VideoPositionItemListener

    init {
        bookmarks.add(0, null) // 헤더 데이터
    }

    interface VideoPositionItemListener {
        fun onItemClickListener(pos: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoPositionViewHolder {
        val view =
            ItemVideoPositionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VideoPositionViewHolder(view, itemListener)
    }

    override fun onBindViewHolder(holder: VideoPositionViewHolder, position: Int) {
        holder.bind(bookmarks[position])
    }

    override fun getItemCount() =
        bookmarks.size

    fun setOnItemListener(_itemListener: VideoPositionItemListener) {
        itemListener = _itemListener
    }

    fun getItem(pos: Int) =
        bookmarks[pos]
}