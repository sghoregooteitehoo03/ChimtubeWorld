package com.sghore.chimtubeworld.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.sghore.chimtubeworld.data.Bookmark
import com.sghore.chimtubeworld.data.ReadHistory

@Dao
interface Dao {
    @Query("SELECT * FROM ReadHistory WHERE articleId = :articleId")
    fun getReadData(articleId: Int): List<ReadHistory>

    @Insert
    suspend fun insertReadData(read: ReadHistory)

    @Insert
    suspend fun insertBookmark(bookmark: Bookmark)
}