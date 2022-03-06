package com.sghore.chimtubeworld.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Goods(
    val title: String,
    val price: String,
    val thumbnailImage: String,
    val url: String,
    val type: String
) : Parcelable
