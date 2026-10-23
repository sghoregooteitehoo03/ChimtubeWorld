package com.sghore.chimtubeworld.feature.cafe.impl.cafe_screen

import androidx.paging.PagingData
import com.sghore.chimtubeworld.core.model.Channel
import com.sghore.chimtubeworld.core.model.Post
import kotlinx.coroutines.flow.Flow

internal data class CafeScreenState(
    val cafeInfo: Channel? = null,
    var cafeCategoryId: Int = 0,
    var cafePosts: Flow<PagingData<Post>>? = null,
    var readHistory: Map<Int, Boolean> = mapOf(),
    val errorMsg: String = ""
)