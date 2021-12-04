package com.sghore.chimtubeworld.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sghore.chimtubeworld.adapter.viewholder.MainContentViewHolder
import com.sghore.chimtubeworld.data.Channel
import com.sghore.chimtubeworld.databinding.ItemContentMainBinding

class MainContentAdapter() : RecyclerView.Adapter<MainContentViewHolder>() {
    private var list = listOf<Channel>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainContentViewHolder {
        val view = ItemContentMainBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return MainContentViewHolder(view)
    }

    override fun onBindViewHolder(holder: MainContentViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount() =
        list.size

    fun syncData(_list: List<Channel>) {
        list = _list
        notifyDataSetChanged()
    }
}