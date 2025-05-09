package com.sghore.chimtubeworld.di

import android.content.Context
import androidx.room.Room
import com.google.firebase.firestore.FirebaseFirestore
import com.sghore.chimtubeworld.data.db.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideFirestore() =
        FirebaseFirestore.getInstance()

    @Singleton
    @Provides
    fun provideRetrofit() =
        Retrofit.Builder()
            .client(
                OkHttpClient.Builder().apply {
                    readTimeout(1, TimeUnit.MINUTES)
                    addInterceptor { chain ->
                        val request = chain.request().newBuilder()
                            .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)")
                            .build()
                        chain.proceed(request)
                    }
                }.build()
            )
            .addConverterFactory(GsonConverterFactory.create())

    @Singleton
    @Provides
    fun getAppDatabase(@ApplicationContext context: Context) =
        Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "ChimtubeWorld"
        ).build()

    @Singleton
    @Provides
    fun getDao(database: AppDatabase) =
        database.getDao()
}