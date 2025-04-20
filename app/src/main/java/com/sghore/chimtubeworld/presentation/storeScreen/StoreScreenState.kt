package com.sghore.chimtubeworld.presentation.storeScreen

import com.sghore.chimtubeworld.R
import com.sghore.chimtubeworld.data.model.GoodsChannelInfo

data class StoreScreenState(
    val storeInfoList: List<GoodsChannelInfo> = listOf(
        GoodsChannelInfo(
            channelImage = R.drawable.marple,
            channelName = "침착맨",
            url = "https://smartstore.naver.com/chim_chungja/best?cp=1"
        ),
        GoodsChannelInfo(
            channelImage = R.drawable.naver,
            channelName = "얼렁뚱땅 상점",
            url = "https://uldd.net/collabochim"
        )
    ),
    val isLoading: Boolean = false,
    val errorMsg: String = ""
)