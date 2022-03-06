package com.sghore.chimtubeworld

import com.sghore.chimtubeworld.data.retrofit.RetrofitService
import com.sghore.chimtubeworld.other.Contents
import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import org.jsoup.Connection
import org.jsoup.Jsoup
import org.junit.Test
import org.junit.Assert.*
import retrofit2.Retrofit
import retrofit2.await
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

    //    -32399000 -> 00:01
    @Test
    fun test() {
        val test = "109ace"
        val a = test.split("|")

        println("default: $a, filter: ${a.filterIndexed { index, s -> index != 0 }}")
    }
}