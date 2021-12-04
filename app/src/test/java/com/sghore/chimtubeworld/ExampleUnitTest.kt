package com.sghore.chimtubeworld

import com.sghore.chimtubeworld.other.Contents
import com.sghore.chimtubeworld.retrofit.RetrofitService
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import org.junit.Test

import org.junit.Assert.*
import retrofit2.Retrofit
import retrofit2.awaitResponse
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun test() {
        runBlocking {
            val builder = Retrofit.Builder()
                .baseUrl(Contents.YOUTUBE_API_URL)
                .client(OkHttpClient.Builder().apply {
                    readTimeout(2, TimeUnit.MINUTES)
                }.build())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            val retrofit = builder.create(RetrofitService::class.java)

            val response = retrofit.getChannelInfo("UCUj6rrhMTR9pipbAWBAMvUQ")
                ?.awaitResponse()
            println("res: $response")
        }
    }
}