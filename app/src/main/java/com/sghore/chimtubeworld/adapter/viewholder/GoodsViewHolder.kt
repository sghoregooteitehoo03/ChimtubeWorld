package com.sghore.chimtubeworld.adapter.viewholder

import android.content.res.Resources
import android.util.DisplayMetrics
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sghore.chimtubeworld.data.Post
import com.sghore.chimtubeworld.databinding.ItemGoodsInfoBinding
import kotlin.math.roundToInt


class GoodsViewHolder(
    private val binding: ItemGoodsInfoBinding
) : RecyclerView.ViewHolder(binding.root) {

    init {
        val layoutParams = GridLayoutManager.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        val margin: Float = convertDpToPx(5, itemView.resources)
        layoutParams.setMargins(
            margin.toInt(), margin.toInt(), margin.toInt(),
            margin.toInt()
        )
        itemView.layoutParams = layoutParams
    }

    fun bind(data: Post) {
        binding.goodsData = data
    }

    private fun convertDpToPx(dp: Int, resource: Resources) =
        (dp * (resource.displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT)).roundToInt()
            .toFloat()
}