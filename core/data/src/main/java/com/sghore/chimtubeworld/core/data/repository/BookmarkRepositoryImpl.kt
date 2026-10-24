package com.sghore.chimtubeworld.core.data.repository

import com.sghore.chimtubeworld.core.model.Bookmark
import com.sghore.chimtubeworld.core.database.dao.Dao
import com.sghore.chimtubeworld.core.data.mapper.toEntity
import com.sghore.chimtubeworld.core.domain.repository.BookmarkRepository
import javax.inject.Inject

internal class BookmarkRepositoryImpl @Inject constructor(
    private val dao: Dao
) : BookmarkRepository {
    // 북마크 추가
    override suspend fun addBookmark(bookmark: Bookmark) {
        dao.insertBookmark(bookmark.toEntity())
    }

    // 북마크 삭제
    override suspend fun deleteBookmark(bookmark: Bookmark) {
        dao.deleteBookmark(bookmark.toEntity())
    }

    // 북마크의 아이디를 조회함
    override suspend fun getItemId(bookmark: Bookmark) =
        dao.getBookmarkItemId(
            videoId = bookmark.videoId,
            title = bookmark.title,
            videoPosition = bookmark.videoPosition,
            color = bookmark.color
        )

}
