package com.sghore.chimtubeworld.util

import java.text.DecimalFormat
import kotlin.math.floor

fun parseFollowText(follows: String?): String {
    return follows?.let {
        if (follows.toInt() / 10000 > 0) {
            val result = follows.toDouble() / 10000
            "팔로워 ${floor(result * 10) / 10f}만명"
        } else {
            "팔로워 ${DecimalFormat("#,###").format(follows)}명"
        }
    } ?: ""
}