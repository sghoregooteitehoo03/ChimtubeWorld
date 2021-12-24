package com.sghore.chimtubeworld.adapter.viewholder

import androidx.recyclerview.widget.RecyclerView
import com.sghore.chimtubeworld.adapter.PreviewImageAdapter
import com.sghore.chimtubeworld.databinding.ItemPreviewImageBinding

class PreviewImageViewHolder(
    private val binding: ItemPreviewImageBinding,
    private val itemListener: PreviewImageAdapter.PreviewImageItemListener
) : RecyclerView.ViewHolder(binding.root) {

    init {
        itemView.setOnClickListener {
            itemListener.onImageClickListener(bindingAdapterPosition)
        }
    }

    fun bind(data: String, selectedPos: Int) {
        binding.imageData = data
        binding.isSelected = selectedPos == bindingAdapterPosition
    }
}