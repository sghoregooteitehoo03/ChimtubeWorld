package com.sghore.chimtubeworld.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.sghore.chimtubeworld.data.ReadHistory

@Database(entities = [ReadHistory::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun getDao(): Dao
}