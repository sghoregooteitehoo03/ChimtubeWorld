package com.sghore.chimtubeworld.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sghore.chimtubeworld.adapter.viewholder.SubContentViewHolder
import com.sghore.chimtubeworld.data.Channel
import com.sghore.chimtubeworld.databinding.ItemContentSubBinding

class SubContentAdapter() : RecyclerView.Adapter<SubContentViewHolder>() {
    private lateinit var mListener: SubContentItemListener
    private var list = listOf<Channel?>()

    interface SubContentItemListener {
        fun onSubItemClickListener(pos: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubContentViewHolder {
        val view = ItemContentSubBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return SubContentViewHolder(view, mListener)
    }

    override fun onBindViewHolder(holder: SubContentViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount() =
        list.size

    fun syncData(_list: List<Channel?>) {
        list = _list
        notifyDataSetChanged()
    }

    fun setOnItemListener(_listener: SubContentItemListener) {
        mListener = _listener
    }

    fun getItem(pos: Int) =
        list[pos]
}