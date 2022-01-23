package com.sghore.chimtubeworld.bind

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.facebook.shimmer.ShimmerFrameLayout
import com.sghore.chimtubeworld.R
import com.sghore.chimtubeworld.data.model.Video
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

@BindingAdapter("app:setLoadingListLayout")
fun setLoadingListLayout(view: ShimmerFrameLayout, isLoading: Boolean) {
    if (isLoading) {
        view.visibility = View.VISIBLE
        view.startShimmer()
    } else {
        view.visibility = View.GONE
        view.stopShimmer()
    }
}

@BindingAdapter("app:setImage", "app:setImageRes", requireAll = false)
fun setImage(view: ImageView, stringImage: String?, imageRes: Int?) {
    view.clipToOutline = true

    if (stringImage != null) {
        Glide.with(view.context)
            .load(stringImage)
            .into(view)
    } else if (imageRes != null) {
        Glide.with(view.context)
            .load(imageRes)
            .into(view)
    }
}

@BindingAdapter("app:setImageBlur")
fun setImageBlur(view: ImageView, isBlur: Boolean) {

    if (isBlur) {
        view.setColorFilter(0)
    } else {
        view.setColorFilter(ContextCompat.getColor(view.context, R.color.black_alpha_50))
    }
}

@BindingAdapter("app:setVideoDurationText")
fun setVideoDurationText(view: TextView, duration: Long) {
    val durationStr = SimpleDateFormat(
        "HH:mm:ss",
        Locale.KOREA
    ).format(duration)
    val splitStr = durationStr.split(":", limit = 2)

    view.text = if (splitStr[0].toInt() == 0) {
        splitStr[1]
    } else {
        durationStr
    }
}

// 조회수 -> (1, 10, 100)회 (1.0)천회, (1.0, 10, 100)만회
// 업로드 시간 -> 1초 전, (1분 전, 10분 전), 1시간 전, 1일 전, 1주일 전, 1개월 전, 1년 전
@BindingAdapter("app:setVideoInfoText")
fun setVideoInfoText(view: TextView, videoData: Video?) {
    videoData?.let {
        val views = getViewCountText(it.viewCount)
        val uploadTime = getUploadTimeText(it.uploadTime)

        view.text = "$views | $uploadTime"
    }
}

private fun getViewCountText(viewCount: Long): String {
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

private fun getUploadTimeText(uploadTime: Long): String {
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