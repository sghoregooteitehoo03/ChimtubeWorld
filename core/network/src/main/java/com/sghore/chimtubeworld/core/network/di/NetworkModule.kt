package com.sghore.chimtubeworld.core.network.di

import com.sghore.chimtubeworld.core.network.retrofit.NaverRetrofitService
import com.sghore.chimtubeworld.core.network.retrofit.RetrofitService
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
internal object NetworkModule {
    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient =
        OkHttpClient.Builder().apply {
            readTimeout(1, TimeUnit.MINUTES)
            addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)")
                    .build()
                chain.proceed(request)
            }
        }.build()

    @Provides
    @Singleton
    fun provideYoutubeService(client: OkHttpClient): RetrofitService =
        createRetrofit("https://www.googleapis.com/youtube/v3/", client)
            .create(RetrofitService::class.java)

    @Provides
    @Singleton
    @NaverCafe
    fun provideCafeService(client: OkHttpClient): NaverRetrofitService =
        createRetrofit("https://apis.naver.com/", client)
            .create(NaverRetrofitService::class.java)

    @Provides
    @Singleton
    @Chzzk
    fun provideChzzkService(client: OkHttpClient): NaverRetrofitService =
        createRetrofit("https://api.chzzk.naver.com/service/", client)
            .create(NaverRetrofitService::class.java)

    // URLs stay local so network does not depend on another project module.
    private fun createRetrofit(baseUrl: String, client: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
}
