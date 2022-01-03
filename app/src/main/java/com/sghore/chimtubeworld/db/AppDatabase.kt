package com.sghore.chimtubeworld.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.sghore.chimtubeworld.data.Bookmark
import com.sghore.chimtubeworld.data.ReadHistory

@Database(entities = [ReadHistory::class, Bookmark::class], version = 2)
abstract class AppDatabase : RoomDatabase() {
    abstract fun getDao(): Dao
}