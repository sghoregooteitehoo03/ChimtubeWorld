package com.sghore.chimtubeworld.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sghore.chimtubeworld.adapter.viewholder.GoodsCategoryViewHolder
import com.sghore.chimtubeworld.data.Channel
import com.sghore.chimtubeworld.databinding.ItemGoodsCategoryBinding

class GoodsCategoryAdapter : RecyclerView.Adapter<GoodsCategoryViewHolder>() {
    var previousSelectedPos = -1
    private var storeList = listOf<Channel>()
    private lateinit var itemListener: GoodsCategoryItemListener

    interface GoodsCategoryItemListener {
        fun onCategoryClickListener(pos: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GoodsCategoryViewHolder {
        val view =
            ItemGoodsCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return GoodsCategoryViewHolder(view, itemListener)
    }

    override fun onBindViewHolder(holder: GoodsCategoryViewHolder, position: Int) {
        holder.bind(storeList[position])
    }

    override fun getItemCount() =
        storeList.size

    fun setOnItemListener(_listener: GoodsCategoryItemListener) {
        itemListener = _listener
    }

    fun syncData(_storeList: List<Channel>) {
        storeList = _storeList
        notifyDataSetChanged()
    }

    fun isEmptyList() =
        storeList.isEmpty()

    fun getItem(pos: Int) =
        storeList[pos]

    fun selectCategory(pos: Int) {
        storeList[pos].isSelected = true

        if (previousSelectedPos != -1) { // 이전에 선택한 위치가 존재할 때
            storeList[previousSelectedPos].isSelected = false
            notifyItemChanged(previousSelectedPos)
        }

        notifyItemChanged(pos)
        previousSelectedPos = pos
    }
}