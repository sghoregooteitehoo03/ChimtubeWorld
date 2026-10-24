package com.sghore.chimtubeworld.core.domain.repository

import com.sghore.chimtubeworld.core.model.Bookmark

interface BookmarkRepository {
    suspend fun addBookmark(bookmark: Bookmark)
    suspend fun deleteBookmark(bookmark: Bookmark)
    suspend fun getItemId(bookmark: Bookmark): Int
}
