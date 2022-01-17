package com.sghore.chimtubeworld.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sghore.chimtubeworld.adapter.viewholder.CafeCategoryViewHolder
import com.sghore.chimtubeworld.data.model.CafeCategory
import com.sghore.chimtubeworld.databinding.ItemCafeCategoryBinding

class CafeCategoryAdapter : RecyclerView.Adapter<CafeCategoryViewHolder>() {
    var previousSelectedPos = -1
    private val categoryList =
        listOf(
            CafeCategory("전체", -1),
            CafeCategory("방송일정 및 공지", 5),
            CafeCategory("침착맨 전용", 42),
            CafeCategory("침착맨 갤러리", 33),
            CafeCategory("침착맨 이야기", 1),
            CafeCategory("팬아트", 2),
            CafeCategory("침착맨 짤", 6),
            CafeCategory("추천영상", 55),
            CafeCategory("해줘요", 4),
            CafeCategory("찾아주세요", 56)
        )
    private lateinit var itemListener: CafeCategoryItemListener

    interface CafeCategoryItemListener {
        fun onCategoryClickListener(pos: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CafeCategoryViewHolder {
        val view =
            ItemCafeCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CafeCategoryViewHolder(view, itemListener)
    }

    override fun onBindViewHolder(holder: CafeCategoryViewHolder, position: Int) {
        holder.bind(categoryList[position])
    }

    override fun getItemCount() = categoryList.size

    fun setOnItemListener(_listener: CafeCategoryItemListener) {
        itemListener = _listener
    }

    fun getCategoryId(pos: Int) =
        categoryList[pos].categoryId

    // 카테고리 선택
    fun selectCategory(pos: Int) {
        categoryList[pos].isSelected = true

        if (previousSelectedPos != -1) { // 이전에 선택한 위치가 존재할 때
            categoryList[previousSelectedPos].isSelected = false
            notifyItemChanged(previousSelectedPos)
        }

        notifyItemChanged(pos)
        previousSelectedPos = pos
    }
}