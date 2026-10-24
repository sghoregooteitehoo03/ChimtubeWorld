package com.sghore.chimtubeworld.core.data.datasource

import com.sghore.chimtubeworld.core.database.dao.Dao
import com.sghore.chimtubeworld.core.database.entity.BookmarkEntity
import com.sghore.chimtubeworld.core.database.entity.ReadHistoryEntity

/** 호출 시도와 성공한 변경을 구분한다. SQL이나 Room 동작을 흉내 내지 않는다. */
internal class SpyDao : Dao {
    var readData: List<ReadHistoryEntity> = emptyList()
    var bookmarks: List<BookmarkEntity> = emptyList()
    var itemId: Int = 42
    var getReadFailure: Throwable? = null
    var insertReadFailure: Throwable? = null
    var getBookmarksFailure: Throwable? = null
    var getItemIdFailure: Throwable? = null
    var insertBookmarkFailure: Throwable? = null
    var deleteBookmarkFailure: Throwable? = null

    data class ItemIdRequest(
        val videoId: String,
        val title: String,
        val videoPosition: Long,
        val color: Int
    )

    private val _readRequests = mutableListOf<Int>()
    val readRequests: List<Int>
        get() = _readRequests.toList()
    private val _readInsertAttempts = mutableListOf<ReadHistoryEntity>()
    val readInsertAttempts: List<ReadHistoryEntity>
        get() = _readInsertAttempts.toList()
    private val _savedReadData = mutableListOf<ReadHistoryEntity>()
    val savedReadData: List<ReadHistoryEntity>
        get() = _savedReadData.toList()
    private val _bookmarkRequests = mutableListOf<String>()
    val bookmarkRequests: List<String>
        get() = _bookmarkRequests.toList()
    private val _itemIdRequests = mutableListOf<ItemIdRequest>()
    val itemIdRequests: List<ItemIdRequest>
        get() = _itemIdRequests.toList()
    private val _bookmarkInsertAttempts = mutableListOf<BookmarkEntity>()
    val bookmarkInsertAttempts: List<BookmarkEntity>
        get() = _bookmarkInsertAttempts.toList()
    private val _savedBookmarks = mutableListOf<BookmarkEntity>()
    val savedBookmarks: List<BookmarkEntity>
        get() = _savedBookmarks.toList()
    private val _bookmarkDeleteAttempts = mutableListOf<BookmarkEntity>()
    val bookmarkDeleteAttempts: List<BookmarkEntity>
        get() = _bookmarkDeleteAttempts.toList()
    private val _deletedBookmarks = mutableListOf<BookmarkEntity>()
    val deletedBookmarks: List<BookmarkEntity>
        get() = _deletedBookmarks.toList()

    override suspend fun getReadData(articleId: Int): List<ReadHistoryEntity> {
        _readRequests += articleId
        getReadFailure?.let { throw it }
        return readData.filter { it.articleId == articleId }
    }

    override suspend fun insertReadData(read: ReadHistoryEntity) {
        _readInsertAttempts += read
        insertReadFailure?.let { throw it }
        _savedReadData += read
    }

    override suspend fun getBookmarks(videoId: String): List<BookmarkEntity> {
        _bookmarkRequests += videoId
        getBookmarksFailure?.let { throw it }
        return bookmarks.filter { it.videoId == videoId }
    }

    override suspend fun getBookmarkItemId(
        videoId: String,
        title: String,
        videoPosition: Long,
        color: Int
    ): Int {
        _itemIdRequests += ItemIdRequest(videoId, title, videoPosition, color)
        getItemIdFailure?.let { throw it }
        return itemId
    }

    override suspend fun insertBookmark(bookmark: BookmarkEntity) {
        _bookmarkInsertAttempts += bookmark
        insertBookmarkFailure?.let { throw it }
        _savedBookmarks += bookmark
    }

    override suspend fun deleteBookmark(bookmark: BookmarkEntity) {
        _bookmarkDeleteAttempts += bookmark
        deleteBookmarkFailure?.let { throw it }
        _deletedBookmarks += bookmark
    }
}
