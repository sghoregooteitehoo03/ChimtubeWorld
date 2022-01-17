package com.sghore.chimtubeworld.data.db

import androidx.room.*
import androidx.room.Dao
import com.sghore.chimtubeworld.data.model.Bookmark
import com.sghore.chimtubeworld.data.model.ReadHistory

@Dao
interface Dao {
    @Query("SELECT * FROM ReadHistory WHERE articleId = :articleId")
    fun getReadData(articleId: Int): List<ReadHistory>

    @Insert
    suspend fun insertReadData(read: ReadHistory)

    @Query("SELECT * FROM Bookmark WHERE video_id = :videoId ORDER BY video_position ASC")
    fun getBookmarks(videoId: String): List<Bookmark>

    @Insert
    suspend fun insertBookmark(bookmark: Bookmark)

    @Update
    suspend fun updateBookmark(bookmark: Bookmark)

    @Delete
    suspend fun deleteBookmark(bookmark: Bookmark)
}