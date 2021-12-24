package com.sghore.chimtubeworld.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sghore.chimtubeworld.adapter.viewholder.GoodsViewHolder
import com.sghore.chimtubeworld.data.Goods
import com.sghore.chimtubeworld.data.Post
import com.sghore.chimtubeworld.databinding.ItemGoodsInfoBinding

class GoodsAdapter : RecyclerView.Adapter<GoodsViewHolder>() {
    private var goodsList = listOf<Goods>()
    private lateinit var itemListener: GoodsItemListener

    interface GoodsItemListener {
        fun onGoodsClickListener(pos: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GoodsViewHolder {
        val view =
            ItemGoodsInfoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return GoodsViewHolder(view, itemListener)
    }

    override fun onBindViewHolder(holder: GoodsViewHolder, position: Int) {
        holder.bind(goodsList[position])
    }

    override fun getItemCount() =
        goodsList.size

    fun setOnItemListener(_itemListener: GoodsItemListener) {
        itemListener = _itemListener
    }

    fun syncData(_goodsList: List<Goods>) {
        goodsList = _goodsList
        notifyDataSetChanged()
    }
}