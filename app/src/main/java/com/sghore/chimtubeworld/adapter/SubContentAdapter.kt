package com.sghore.chimtubeworld.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sghore.chimtubeworld.adapter.viewholder.SubContentViewHolder
import com.sghore.chimtubeworld.data.Channel
import com.sghore.chimtubeworld.databinding.ItemContentSubBinding

class SubContentAdapter() : RecyclerView.Adapter<SubContentViewHolder>() {
    private var list = listOf<Channel>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubContentViewHolder {
        val view = ItemContentSubBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return SubContentViewHolder(view)
    }

    override fun onBindViewHolder(holder: SubContentViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount() =
        list.size

    fun syncData(_list: List<Channel>) {
        list = _list
        notifyDataSetChanged()
    }
}