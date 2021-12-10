package com.sghore.chimtubeworld.bind

import android.graphics.Color
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.sghore.chimtubeworld.R
import com.sghore.chimtubeworld.data.Post
import java.text.DecimalFormat
import kotlin.math.floor

@BindingAdapter("app:setImage")
fun setImage(view: ImageView, stringImage: String?) {
    view.clipToOutline = true

    if (stringImage != null) {
        Glide.with(view.context)
            .load(stringImage)
            .into(view)
    }
}

@BindingAdapter("app:setTwitchBroadcastImage", "app:isBlur", requireAll = false)
fun setTwitchBroadcastImage(view: ImageView, stringImage: String?, isBlur: Boolean = false) {
    view.clipToOutline = true

    if (stringImage != null) {
        Glide.with(view.context)
            .load(stringImage)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true)
            .into(view)
    }
    if (isBlur) {
        view.setColorFilter(ContextCompat.getColor(view.context, R.color.black_blur))
    }
}

@BindingAdapter("app:setCafePostDate")
fun setCafePostDate(view: TextView, postData: Post?) {
    view.text = "${postData?.userName}  |  ${postData?.postDate}"
}

@BindingAdapter("app:setFollowCount")
fun setFollowCount(view: TextView, follows: String?) {
    if (follows != null) {
        view.text = if (follows.toInt() / 10000 > 0) {
            val result = follows.toDouble() / 10000
            "팔로워 ${floor(result * 10) / 10f}만명"
        } else {
            "팔로워 ${DecimalFormat("#,###").format(follows)}명"
        }
    }
}