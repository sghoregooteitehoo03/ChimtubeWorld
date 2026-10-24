package com.sghore.chimtubeworld.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.sghore.chimtubeworld.core.database.dao.Dao
import com.sghore.chimtubeworld.core.database.entity.BookmarkEntity
import com.sghore.chimtubeworld.core.database.entity.ReadHistoryEntity

@Database(entities = [ReadHistoryEntity::class, BookmarkEntity::class], version = 2, exportSchema = false)
internal abstract class AppDatabase : RoomDatabase() {
    abstract fun getDao(): Dao
}
