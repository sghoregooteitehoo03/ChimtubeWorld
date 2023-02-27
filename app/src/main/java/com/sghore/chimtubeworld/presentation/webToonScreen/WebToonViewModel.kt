package com.sghore.chimtubeworld.presentation.webToonScreen

import androidx.lifecycle.ViewModel
import com.sghore.chimtubeworld.R
import com.sghore.chimtubeworld.data.model.Channel
import com.sghore.chimtubeworld.other.Contents
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class WebToonViewModel @Inject constructor() : ViewModel() {
    val webToonInfos = listOf(
        Channel(
            id = "103759", // 웹툰 앱에서 바로가기에 필요한 웹툰 아이디 정보
            name = "이말년씨리즈",
            explains = arrayOf("2009~2012"),
            image = R.drawable.webtoon_cover_1.toString(),
            url = Contents.WEBTOON_URL_1,
            type = 0
        ),
        Channel(
            id = "704595",
            name = "이말년씨리즈 2018",
            explains = arrayOf("2018"),
            image = R.drawable.webtoon_cover_2.toString(),
            url = Contents.WEBTOON_URL_2,
            type = 0
        ),
        Channel(
            id = "602921",
            name = "이말년 서유기",
            explains = arrayOf("2013~2016"),
            image = R.drawable.webtoon_cover_3.toString(),
            url = Contents.WEBTOON_URL_3,
            type = 0
        ),
        Channel(
            id = "557527",
            name = "맨 vs 던전",
            explains = arrayOf("2013"),
            image = R.drawable.webtoon_cover_4.toString(),
            url = Contents.WEBTOON_URL_4,
            type = 0
        ),
        Channel(
            id = "687127",
            name = "본격 말년런 만화",
            explains = arrayOf("2016"),
            image = R.drawable.webtoon_cover_5.toString(),
            url = Contents.WEBTOON_URL_5,
            type = 0
        )
    )
}