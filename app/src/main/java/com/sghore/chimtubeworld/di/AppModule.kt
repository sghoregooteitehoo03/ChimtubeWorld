package com.sghore.chimtubeworld.di

import com.google.firebase.firestore.FirebaseFirestore
import com.sghore.chimtubeworld.other.Contents
import com.sghore.chimtubeworld.retrofit.RetrofitService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
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
            .baseUrl(Contents.YOUTUBE_API_URL)
            .client(OkHttpClient.Builder().apply {
                readTimeout(2, TimeUnit.MINUTES)
            }.build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Singleton
    @Provides
    fun provideRetrofitService(retrofit: Retrofit) =
        retrofit.create(RetrofitService::class.java)
}