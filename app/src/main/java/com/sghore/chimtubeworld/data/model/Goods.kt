package com.sghore.chimtubeworld.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Goods(
    val title: String,
    val price: Int,
    val thumbnailImage: String,
    val previewImages: List<String>,
    val url: String,
) : Parcelable
