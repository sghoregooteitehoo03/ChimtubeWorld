package com.sghore.chimtubeworld.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sghore.chimtubeworld.adapter.viewholder.TwitchUserViewHolder
import com.sghore.chimtubeworld.data.Channel
import com.sghore.chimtubeworld.databinding.ItemCrewBinding

class TwitchUserAdapter() : RecyclerView.Adapter<TwitchUserViewHolder>() {
    private var userList = listOf<Channel?>()
    private lateinit var listener: TwitchUserItemListener

    interface TwitchUserItemListener {
        fun onItemClickListener(pos: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TwitchUserViewHolder {
        val view = ItemCrewBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return TwitchUserViewHolder(view, listener)
    }

    override fun onBindViewHolder(holder: TwitchUserViewHolder, position: Int) {
        holder.bind(userList[position])
    }

    override fun getItemCount() =
        userList.size

    fun syncData(_userList: List<Channel?>) {
        userList = _userList
        notifyDataSetChanged()
    }

    fun setOnItemListener(_listener: TwitchUserItemListener) {
        listener = _listener
    }

    fun getItem(pos: Int) =
        userList[pos]
}