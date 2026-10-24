package com.sghore.chimtubeworld.core.database.di

import android.content.Context
import androidx.room.Room
import com.sghore.chimtubeworld.core.database.AppDatabase
import com.sghore.chimtubeworld.core.database.dao.Dao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object DatabaseModule {
    @Singleton
    @Provides
    fun getAppDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "ChimtubeWorld"
        ).build()

    @Singleton
    @Provides
    fun getDao(database: AppDatabase): Dao =
        database.getDao()
}
