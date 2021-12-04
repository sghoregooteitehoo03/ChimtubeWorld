package com.sghore.chimtubeworld.bind

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide

@BindingAdapter("app:setImage")
fun setImage(view: ImageView, stringImage: String?) {
    view.clipToOutline = true

    if (stringImage != null) {
        Glide.with(view.context)
            .load(stringImage)
            .into(view)
    }
}