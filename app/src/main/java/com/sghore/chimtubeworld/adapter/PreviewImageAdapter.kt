package com.sghore.chimtubeworld.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sghore.chimtubeworld.adapter.viewholder.PreviewImageViewHolder
import com.sghore.chimtubeworld.databinding.ItemPreviewImageBinding

class PreviewImageAdapter : RecyclerView.Adapter<PreviewImageViewHolder>() {
    var previousSelectedPos = -1
    private var selectedPos = 0
    private var images = listOf<String>()
    private lateinit var itemListener: PreviewImageItemListener

    interface PreviewImageItemListener {
        fun onImageClickListener(pos: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PreviewImageViewHolder {
        val view =
            ItemPreviewImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PreviewImageViewHolder(view, itemListener)
    }

    override fun onBindViewHolder(holder: PreviewImageViewHolder, position: Int) {
        holder.bind(images[position], selectedPos)
    }

    override fun getItemCount() =
        images.size

    fun setOnItemListener(_itemListener: PreviewImageItemListener) {
        itemListener = _itemListener
    }

    fun syncData(_images: List<String>) {
        images = _images
        notifyDataSetChanged()
    }

    fun selectedImage(pos: Int) {
        selectedPos = pos

        if (previousSelectedPos != -1) { // 이전에 선택한 위치가 존재할 때
            notifyItemChanged(previousSelectedPos)
        }

        notifyItemChanged(pos)
        previousSelectedPos = pos
    }

    fun getItem(pos: Int) =
        images[pos]
}