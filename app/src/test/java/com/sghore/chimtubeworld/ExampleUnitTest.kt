package com.sghore.chimtubeworld

import com.sghore.chimtubeworld.other.Contents
import com.sghore.chimtubeworld.retrofit.RetrofitService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import org.junit.Test

import org.junit.Assert.*
import retrofit2.Retrofit
import retrofit2.await
import retrofit2.awaitResponse
import retrofit2.converter.gson.GsonConverterFactory
import java.text.DecimalFormat
import java.util.concurrent.TimeUnit
import kotlin.math.floor

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
    fun followsTexttest() {
        val follows = "500"
        if (follows.toInt() / 10000 > 0) {
            val divideResult = follows.toDouble() / 10000
            println("팔로워 ${floor(divideResult * 10) / 10f}만명")
        } else {
            val divideResult = follows.toInt()
            println("팔로워 ${DecimalFormat("#,###").format(divideResult)}명")
        }
    }

    @Test
    fun retrofitTest() {
        runBlocking {
            val builder = Retrofit.Builder()
                .baseUrl(Contents.TWITCH_API_URL)
                .client(OkHttpClient.Builder().apply {
                    readTimeout(2, TimeUnit.MINUTES)
                }.build())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            val retrofit = builder.create(RetrofitService::class.java)
//            val result = async {
//                retrofit.getTUserInfo(
//                    "Bearer 3u0x3d7dngj9jormp20nw4zd6b3ls0",
//                    "zoodasa"
//                )?.await()
//
//
//                "C"
//            }.await()

            val result = retrofit.getTUserStream(
                "Bearer 3u0x3d7dngj9jormp20nw4zd6b3ls0",
                "zilioner"
            )?.await()

//            val test = response?.data
//                ?.get(0)
//                ?.thumbnailUrl
//                ?.replace("{width}", "1920")
//                ?.replace("{height}", "1080")

            println("res: $result")
            println("끝")
        }
    }
}
//            https://static-cdn.jtvnw.net/previews-ttv/live_user_zoodasa-{width}x{height}.jpg