package com.sghore.chimtubeworld.presentation.cafeScreen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.paging.PagingData
import com.sghore.chimtubeworld.data.model.Channel
import com.sghore.chimtubeworld.data.model.Post
import kotlinx.coroutines.flow.Flow

data class CafeScreenState(
    val cafeInfo: Channel? = null,
    var cafeCategoryId: Int = -1,
    var cafePosts: Flow<PagingData<Post>>? = null,
    var readHistory: Map<Int, Boolean> = mapOf(),
    val errorMsg: String = ""
)