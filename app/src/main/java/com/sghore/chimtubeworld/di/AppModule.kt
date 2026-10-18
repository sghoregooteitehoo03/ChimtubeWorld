package com.sghore.chimtubeworld.di

import com.google.firebase.firestore.FirebaseFirestore
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
}
