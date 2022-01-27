package com.sghore.chimtubeworld.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.sghore.chimtubeworld.data.model.Bookmark
import com.sghore.chimtubeworld.data.model.ReadHistory

@Database(entities = [ReadHistory::class, Bookmark::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun getDao(): Dao
}