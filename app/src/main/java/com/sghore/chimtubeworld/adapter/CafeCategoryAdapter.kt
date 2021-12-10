package com.sghore.chimtubeworld.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sghore.chimtubeworld.adapter.viewholder.CafeCategoryViewHolder
import com.sghore.chimtubeworld.databinding.ItemCafeCategoryBinding

class CafeCategoryAdapter : RecyclerView.Adapter<CafeCategoryViewHolder>() {
    private val categoryList =
        listOf("전체", "방송일정 및 공지", "침착맨 이야기", "팬아트", "침착맨 짤", "추천영상", "해줘요", "찾아주세요", "침착맨 갤러리")

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CafeCategoryViewHolder {
        val view =
            ItemCafeCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CafeCategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CafeCategoryViewHolder, position: Int) {
        holder.bind(categoryList[position])
    }

    override fun getItemCount() = categoryList.size
}