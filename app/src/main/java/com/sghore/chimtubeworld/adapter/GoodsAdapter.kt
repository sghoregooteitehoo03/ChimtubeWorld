package com.sghore.chimtubeworld.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sghore.chimtubeworld.adapter.viewholder.GoodsViewHolder
import com.sghore.chimtubeworld.data.Post
import com.sghore.chimtubeworld.databinding.ItemGoodsInfoBinding

class GoodsAdapter : RecyclerView.Adapter<GoodsViewHolder>() {
    private var goodsList = listOf<Post>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GoodsViewHolder {
        val view =
            ItemGoodsInfoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return GoodsViewHolder(view)
    }

    override fun onBindViewHolder(holder: GoodsViewHolder, position: Int) {
        holder.bind(goodsList[position])
    }

    override fun getItemCount() =
        goodsList.size

    fun syncData(_goodsList: List<Post>) {
        goodsList = _goodsList
        notifyDataSetChanged()
    }
}