package com.sghore.chimtubeworld.adapter.viewholder

import android.content.res.ColorStateList
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.sghore.chimtubeworld.adapter.VideoPositionAdapter
import com.sghore.chimtubeworld.data.Bookmark
import com.sghore.chimtubeworld.databinding.ItemVideoPositionBinding
import java.text.SimpleDateFormat
import java.util.*

class VideoPositionViewHolder(
    private val binding: ItemVideoPositionBinding,
    private val itemListener: VideoPositionAdapter.VideoPositionItemListener
) : RecyclerView.ViewHolder(binding.root) {

    init {
        itemView.setOnClickListener {
            itemListener.onItemClickListener(bindingAdapterPosition)
        }
    }

    fun bind(data: Bookmark?) {
        if (data != null) {
            binding.bookmarkColorView.visibility = View.VISIBLE
            binding.bookmarkColorView.backgroundTintList = ColorStateList.valueOf(
                data.color
            )
            binding.bookmarkText.text =
                "${data.title} (${getDateStrFromPosition(data.videoPosition)})"
        } else {
            binding.bookmarkColorView.visibility = View.GONE

            binding.bookmarkText.text = "처음부터"
        }
    }

    // 시간 데이터를 시:분:초 String으로 변환하여 반환
    private fun getDateStrFromPosition(videoPosition: Long): String {
        val dateFormat = if (videoPosition >= -28800000) { // 01:00:00
            SimpleDateFormat(
                "hh:mm:ss",
                Locale.KOREA
            )
        } else {
            SimpleDateFormat(
                "mm:ss",
                Locale.KOREA
            )
        }

        return dateFormat.format(videoPosition)
    }
}