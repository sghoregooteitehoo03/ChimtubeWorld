package com.sghore.chimtubeworld.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sghore.chimtubeworld.adapter.viewholder.BookmarkColorViewHolder
import com.sghore.chimtubeworld.databinding.ItemBookmarkColorBinding

class BookmarkColorAdapter : RecyclerView.Adapter<BookmarkColorViewHolder>() {
    private lateinit var itemListener: BookmarkColorItemListener
    private val colorList = listOf(
        Color.parseColor("#FF0000"),
        Color.parseColor("#FF6200"),
        Color.parseColor("#FFEB00"),
        Color.parseColor("#76FF00"),
        Color.parseColor("#0014FF"),
        Color.parseColor("#C400FF"),
        Color.parseColor("#767676"),
        Color.parseColor("#000000")
    )

    var previousSelectedPos = -1
    private var selectedPos = -1

    interface BookmarkColorItemListener {
        fun onItemClickListener(pos: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookmarkColorViewHolder {
        val view =
            ItemBookmarkColorBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BookmarkColorViewHolder(view, itemListener)
    }

    override fun onBindViewHolder(holder: BookmarkColorViewHolder, position: Int) {
        holder.bind(colorList[position], position == selectedPos)
    }

    override fun getItemCount() =
        colorList.size

    fun setOnItemListener(_itemListener: BookmarkColorItemListener) {
        itemListener = _itemListener
    }

    fun selectColor(pos: Int) {
        selectedPos = pos

        if (previousSelectedPos != -1) { // 이전에 선택한 위치가 존재할 때
            notifyItemChanged(previousSelectedPos)
        }

        notifyItemChanged(pos)
        previousSelectedPos = pos
    }

    fun getItem(pos: Int) =
        colorList[pos]
}