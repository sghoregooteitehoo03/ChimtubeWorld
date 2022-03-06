package com.sghore.chimtubeworld.presentation.cafeScreen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.paging.PagingData
import com.sghore.chimtubeworld.data.model.Channel
import com.sghore.chimtubeworld.data.model.Post
import kotlinx.coroutines.flow.Flow

data class CafePostState(
    var cafeCategoryId: Int = -1,
    var cafePosts: Flow<PagingData<Post>>? = null
)

data class CafeScreenState(
    val cafeInfo: Channel? = null,
    val errorMsg: String = ""
) {
    var cafePostState by mutableStateOf(CafePostState())
    var readHistoryState: Map<Int, Boolean> by mutableStateOf(mapOf())
}