package com.sghore.chimtubeworld.presentation.storeScreen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.sghore.chimtubeworld.data.model.Channel
import com.sghore.chimtubeworld.data.model.Goods
import kotlinx.coroutines.flow.Flow

data class StoreScreenState(
    val storeInfoList: List<Channel> = emptyList(),
    val selectedStoreUrl: String = "",
    val goodsList: List<Goods> = emptyList(),
    val isLoading: Boolean = false,
    val errorMsg: String = ""
)