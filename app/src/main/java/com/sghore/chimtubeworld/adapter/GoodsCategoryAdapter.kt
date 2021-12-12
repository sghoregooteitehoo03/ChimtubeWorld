package com.sghore.chimtubeworld.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sghore.chimtubeworld.adapter.viewholder.GoodsCategoryViewHolder
import com.sghore.chimtubeworld.data.Channel
import com.sghore.chimtubeworld.databinding.ItemGoodsCategoryBinding

class GoodsCategoryAdapter : RecyclerView.Adapter<GoodsCategoryViewHolder>() {
    private var storeList = listOf<Channel>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GoodsCategoryViewHolder {
        val view =
            ItemGoodsCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return GoodsCategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: GoodsCategoryViewHolder, position: Int) {
        holder.bind(storeList[position])
    }

    override fun getItemCount() =
        storeList.size

    fun syncData(_storeList: List<Channel>) {
        storeList = _storeList
        notifyDataSetChanged()
    }
}