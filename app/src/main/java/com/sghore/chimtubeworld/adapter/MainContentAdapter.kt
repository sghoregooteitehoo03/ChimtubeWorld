package com.sghore.chimtubeworld.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sghore.chimtubeworld.adapter.viewholder.MainContentViewHolder
import com.sghore.chimtubeworld.data.Channel
import com.sghore.chimtubeworld.databinding.ItemContentMainBinding

class MainContentAdapter() : RecyclerView.Adapter<MainContentViewHolder>() {
    private lateinit var mListener: MainContentItemListener
    private var list = listOf<Channel?>()

    interface MainContentItemListener {
        fun onMainItemClickListener(pos: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainContentViewHolder {
        val view = ItemContentMainBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return MainContentViewHolder(view, mListener)
    }

    override fun onBindViewHolder(holder: MainContentViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount() =
        list.size

    fun syncData(_list: List<Channel?>) {
        list = _list
        notifyDataSetChanged()
    }

    fun setOnItemListener(_listener: MainContentItemListener) {
        mListener = _listener
    }

    fun getItem(pos: Int) =
        list[pos]
}