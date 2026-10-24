package com.sghore.chimtubeworld.core.database.dao

import androidx.room.*
import androidx.room.Dao
import com.sghore.chimtubeworld.core.database.entity.BookmarkEntity
import com.sghore.chimtubeworld.core.database.entity.ReadHistoryEntity

@Dao
interface Dao {
    @Query("SELECT * FROM ReadHistory WHERE articleId = :articleId")
    suspend fun getReadData(articleId: Int): List<ReadHistoryEntity>

    @Insert
    suspend fun insertReadData(read: ReadHistoryEntity)

    @Query("SELECT * FROM Bookmark WHERE video_id = :videoId ORDER BY video_position ASC")
    suspend fun getBookmarks(videoId: String): List<BookmarkEntity>

    @Query("SELECT id FROM Bookmark WHERE video_id = :videoId AND title = :title AND video_position = :videoPosition AND color = :color")
    suspend fun getBookmarkItemId(
        videoId: String,
        title: String,
        videoPosition: Long,
        color: Int
    ): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBookmark(bookmark: BookmarkEntity)

    @Delete
    suspend fun deleteBookmark(bookmark: BookmarkEntity)
}
