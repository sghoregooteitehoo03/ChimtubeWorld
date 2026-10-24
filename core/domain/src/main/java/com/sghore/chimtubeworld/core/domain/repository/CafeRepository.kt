package com.sghore.chimtubeworld.core.domain.repository

import androidx.paging.PagingData
import com.sghore.chimtubeworld.core.model.Channel
import com.sghore.chimtubeworld.core.model.Post
import kotlinx.coroutines.flow.Flow

interface CafeRepository {
    fun getCafePosts(categoryId: Int): Flow<PagingData<Post>>
    suspend fun getCafeInfo(): Channel
    suspend fun readPost(articleId: Int)
}
