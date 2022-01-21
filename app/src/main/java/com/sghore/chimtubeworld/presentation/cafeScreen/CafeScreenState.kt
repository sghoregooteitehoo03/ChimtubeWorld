package com.sghore.chimtubeworld.presentation.cafeScreen

import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import com.sghore.chimtubeworld.data.model.Channel
import com.sghore.chimtubeworld.data.model.Post
import kotlinx.coroutines.flow.Flow

data class CafeScreenState(
    val cafeInfo: Channel? = null,
    val cafePosts: Flow<PagingData<Post>>? = null,
    val cafeCategoryId: Int = -1,
    val errorMsg: String = ""
)
