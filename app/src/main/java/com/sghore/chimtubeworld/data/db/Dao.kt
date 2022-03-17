package com.sghore.chimtubeworld.data.db

import androidx.room.*
import androidx.room.Dao
import com.sghore.chimtubeworld.data.model.Bookmark
import com.sghore.chimtubeworld.data.model.ReadHistory

@Dao
interface Dao {
    @Query("SELECT * FROM ReadHistory WHERE articleId = :articleId")
    suspend fun getReadData(articleId: Int): List<ReadHistory>

    @Insert
    suspend fun insertReadData(read: ReadHistory)

    @Query("SELECT * FROM Bookmark WHERE video_id = :videoId ORDER BY video_position ASC")
    suspend fun getBookmarks(videoId: String): List<Bookmark>

    @Query("SELECT id FROM Bookmark WHERE video_id = :videoId AND title = :title AND video_position = :videoPosition AND color = :color")
    suspend fun getBookmarkItemId(
        videoId: String,
        title: String,
        videoPosition: Long,
        color: Int
    ): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBookmark(bookmark: Bookmark)

    @Delete
    suspend fun deleteBookmark(bookmark: Bookmark)
}