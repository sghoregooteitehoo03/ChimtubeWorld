package com.sghore.chimtubeworld.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sghore.chimtubeworld.adapter.viewholder.WebToonViewHolder
import com.sghore.chimtubeworld.data.Channel
import com.sghore.chimtubeworld.databinding.ItemWebtoonBinding

class WebToonAdapter : RecyclerView.Adapter<WebToonViewHolder>() {
    private var webToonList = listOf<Channel>()
    private lateinit var itemListener: WebToonItemListener

    interface WebToonItemListener {
        fun onItemClickListener(pos: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WebToonViewHolder {
        val view =
            ItemWebtoonBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return WebToonViewHolder(view, itemListener)
    }

    override fun onBindViewHolder(holder: WebToonViewHolder, position: Int) {
        holder.bind(webToonList[position])
    }

    override fun getItemCount() =
        webToonList.size

    fun syncData(_webToonList: List<Channel>) {
        webToonList = _webToonList
        notifyDataSetChanged()
    }

    fun setOnItemListener(_listener: WebToonItemListener) {
        itemListener = _listener
    }

    fun getItem(pos: Int) =
        webToonList[pos]
}