package com.sghore.chimtubeworld.data.repository

import com.sghore.chimtubeworld.core.model.Bookmark
import com.sghore.chimtubeworld.core.database.dao.Dao
import com.sghore.chimtubeworld.data.mapper.toEntity
import javax.inject.Inject

class BookmarkRepository @Inject constructor(
    private val dao: Dao
) {
    // 북마크 추가
    suspend fun addBookmark(bookmark: Bookmark) {
        dao.insertBookmark(bookmark.toEntity())
    }

    // 북마크 삭제
    suspend fun deleteBookmark(bookmark: Bookmark) {
        dao.deleteBookmark(bookmark.toEntity())
    }

    // 북마크의 아이디를 조회함
    suspend fun getItemId(bookmark: Bookmark) =
        dao.getBookmarkItemId(
            videoId = bookmark.videoId,
            title = bookmark.title,
            videoPosition = bookmark.videoPosition,
            color = bookmark.color
        )

}