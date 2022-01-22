package com.sghore.chimtubeworld.util

import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
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

fun parseVideoDurationText(duration: Long): String {
    val durationStr = SimpleDateFormat(
        "HH:mm:ss",
        Locale.KOREA
    ).format(duration)
    val splitStr = durationStr.split(":", limit = 2)

    return if (splitStr[0].toInt() == 0) {
        splitStr[1]
    } else {
        durationStr
    }
}

fun parseViewCountText(viewCount: Long): String {
    val decimalFormat = DecimalFormat("#.#").apply {
        this.roundingMode = RoundingMode.FLOOR
    }

    return when {
        viewCount / 100000 > 0 -> {
            val viewCountCal = viewCount / 10000
            "조회수 ${viewCountCal}만회"
        }
        viewCount / 10000 > 0 -> {
            val viewCountCal = viewCount / 10000f
            "조회수 ${decimalFormat.format(viewCountCal)}만회"
        }
        viewCount / 1000 > 0 -> {
            val viewCountCal = viewCount / 1000f
            "조회수 ${decimalFormat.format(viewCountCal)}천회"
        }
        else -> {
            "조회수 ${viewCount}회"
        }
    }
}

fun parseUploadTimeText(uploadTime: Long): String {
    val currentTime = System.currentTimeMillis()
    val diffTime = currentTime - uploadTime

    return when {
        diffTime / 31556926000 > 0 -> { // 년
            val calcTime = diffTime / 31556926000
            "${calcTime}년 전"
        }
        diffTime / 2629743000 > 0 -> { // 월
            val calcTime = diffTime / 2629743000
            "${calcTime}개월 전"
        }
        diffTime / 604800000 > 0 -> { // 주일
            val calcTime = diffTime / 604800000
            "${calcTime}주일 전"
        }
        diffTime / 86400000 > 0 -> { // 일
            val calcTime = diffTime / 86400000
            "${calcTime}일 전"
        }
        diffTime / 3600000 > 0 -> { // 시간
            val calcTime = diffTime / 3600000
            "${calcTime}시간 전"
        }
        diffTime / 60000 > 0 -> { // 분
            val calcTime = (diffTime / 60000)
            "${calcTime}분 전"
        }
        else -> { // 초
            val calcTime = (diffTime / 1000)
            "${calcTime}초 전"
        }
    }
}