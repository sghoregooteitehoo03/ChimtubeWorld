package com.sghore.chimtubeworld.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sghore.chimtubeworld.adapter.viewholder.BookmarkViewHolder
import com.sghore.chimtubeworld.data.Bookmark
import com.sghore.chimtubeworld.databinding.ItemBookmarkBinding

class BookmarkAdapter(private val bookmarks: List<Bookmark>) :
    RecyclerView.Adapter<BookmarkViewHolder>() {
    private lateinit var itemListener: BookmarkItemListener

    interface BookmarkItemListener {
        fun onBookmarkClickListener(pos: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookmarkViewHolder {
        val view = ItemBookmarkBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BookmarkViewHolder(view, itemListener)
    }

    override fun onBindViewHolder(holder: BookmarkViewHolder, position: Int) {
        holder.bind(bookmarks[position])
    }

    override fun getItemCount() =
        bookmarks.size

    fun setOnItemListener(_itemListener: BookmarkItemListener) {
        itemListener = _itemListener
    }

    fun getItem(pos: Int) =
        bookmarks[pos]
}