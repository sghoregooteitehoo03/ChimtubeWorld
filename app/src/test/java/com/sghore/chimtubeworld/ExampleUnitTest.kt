package com.sghore.chimtubeworld

import com.sghore.chimtubeworld.other.Contents
import com.sghore.chimtubeworld.retrofit.RetrofitService
import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import org.jsoup.Connection
import org.jsoup.Jsoup
import org.junit.Test

import org.junit.Assert.*
import retrofit2.Retrofit
import retrofit2.await
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.*
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
    fun retrofitTest() {
        // 109ace, 1983kej, rooftopcat99, yagubu, zilioner, kiyulking, noizemasta, hanryang1125
        runBlocking {
            val builder = Retrofit.Builder()
                .baseUrl(Contents.TWITCH_API_URL)
                .client(OkHttpClient.Builder().apply {
                    readTimeout(2, TimeUnit.MINUTES)
                }.build())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            val retrofit = builder.create(RetrofitService::class.java)
            val array = arrayOf(
                "109ace",
                "1983kej",
                "rooftopcat99",
                "yagubu",
                "zilioner",
                "kiyulking",
                "noizemasta",
                "hanryang1125"
            )

//            val result = retrofit.test(
//                "Bearer 3u0x3d7dngj9jormp20nw4zd6b3ls0",
//                array
//            ).await()
//            val channelList = arrayOfNulls<String>(array.size)
//                .toMutableList()
//
//            result.data.forEach {
//                val index = array.indexOf(it.login)
//                channelList[index] = it.login
//            }
//            println("res: ${channelList}}")
            println("끝")
        }
    }

    //    https://cafe.naver.com/zilioner?iframe_url=/ArticleList.nhn%3Fsearch.clubid=29646865%26search.boardtype=C%26search.page=1%26userDisplay=10
//    https://cafe.naver.com/zilioner?iframe_url=/ArticleList.nhn%3Fsearch.clubid=29646865%26search.menuid={61}%26search.boardtype=L%26search.page={1}%26userDisplay=10
    @Test
    fun jsoupTest() {
        val mainUrl =
            "https://cafe.naver.com/ArticleList.nhn?search.clubid=29646865&search.menuid=61&search.boardtype=C&search.page=1&userDisplay=10"
        val response1 = Jsoup.connect(mainUrl)
            .userAgent("19.0.1.84.52")
            .header(
                "accept",
                "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9"
            )
            .header("accept-encoding", "gzip, deflate, br")
            .header("accept-language", "ko-KR,ko;q=0.9")
            .method(Connection.Method.GET)
            .get()

        val response2 =
            Jsoup.connect("https://cafe.naver.com/ArticleList.nhn?search.clubid=29646865&search.menuid=61&search.boardtype=L&search.totalCount=6&search.cafeId=29646865&search.page=1")
                .userAgent("19.0.1.84.52")
                .header(
                    "accept",
                    "accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9"
                )
                .header("accept-encoding", "gzip, deflate, br")
                .header("accept-language", "ko-KR,ko;q=0.9")
                .method(Connection.Method.GET)
                .get()

        val postDocs = response1.select("div#main-area")
            .select("ul.article-movie-sub li")
        val titleDocs = response2.select("div.article-board")
            .select("tbody tr")
            .filter { it.attr("class").isEmpty() && it.select("td.td_article").isNotEmpty() }

        for ((index, postDoc) in postDocs.withIndex()) {
            val imageDoc = postDoc.select("div.movie-img")
                .select("a")
            val title = titleDocs[index]
                .select("a.article")
                .text()

            val thumbnailImage = imageDoc.select("img")
                .attr("src")
            val userName = postDoc.select("td.p-nick")
                .select("a")
                .text()
            val postDate = postDoc.select("div.date_num")
                .select("span.date")
                .text()

            println("title: $title, image: $thumbnailImage, userName: $userName, date: $postDate")
        }
    }

    @Test
    fun getVideosTest() {
        runBlocking {
            val builder = Retrofit.Builder()
                .baseUrl(Contents.TWITCH_API_URL)
                .client(OkHttpClient.Builder().apply {
                    readTimeout(2, TimeUnit.MINUTES)
                }.build())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            val retrofit = builder.create(RetrofitService::class.java)

            val playlistItems = retrofit.getTVideosFromUserId(
                "Bearer 3u0x3d7dngj9jormp20nw4zd6b3ls0",
                "66375105",
                "eyJiIjpudWxsLCJhIjp7Ik9mZnNldCI6MjB9fQ"
            ).await()

            println("끝")
            println("res: ${playlistItems}")
        }
    }

    //    멋진 동영상이 있어! "떡볶이전사 통닭천사의 떡볶이 쿡방" https://www.twitch.tv/zilioner/v/1248557250?sr=a&t=5062s
//    같이 Twitch에서 햇살살 (hatsalsal) 방송을 보자! https://www.twitch.tv/hatsalsal?sr=a
//    멋진 동영상이 있어! "7시 배도라지와 쉐리님 스타1 합방" https://www.twitch.tv/zilioner/v/1246620300?sr=a&t=14923s
//    https://youtu.be/UY6Nxh7Uz98
//    https://www.wikitree.co.kr/articles/720911

    @Test
    fun test() {
        val dateFormat = SimpleDateFormat(
            "mm:ss",
            Locale.KOREA
        )
        val uploadTime =
            dateFormat.parse("1:30").time
        val uploadTime2 =
            dateFormat.parse("1:30f").time
        println("$uploadTime, $uploadTime2")
    }
}