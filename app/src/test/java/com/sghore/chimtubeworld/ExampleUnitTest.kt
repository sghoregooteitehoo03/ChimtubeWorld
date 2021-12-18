package com.sghore.chimtubeworld

import com.sghore.chimtubeworld.other.Contents
import com.sghore.chimtubeworld.retrofit.RetrofitService
import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import org.jsoup.Connection
import org.jsoup.Jsoup
import org.jsoup.select.Elements
import org.junit.Test

import org.junit.Assert.*
import retrofit2.Retrofit
import retrofit2.await
import retrofit2.awaitResponse
import retrofit2.converter.gson.GsonConverterFactory
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalField
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.floor
import kotlin.time.Duration

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
        // UCUj6rrhMTR9pipbAWBAMvUQ, UCC1LvVTX2ySKYjeIXkAtvsQ, UCewitUbsXnyjvJjGgxa0IYw, UCxQXvvaqwA2NzPXs5775ogw
        // UCAmff0euQRf6RwVlbB8PLMw, UCgiO7Kxib0SZEG0DoeuBkdQ, UCEkcg9WqCX4sGxRQ3uUkekA
        runBlocking {
            val builder = Retrofit.Builder()
                .baseUrl(Contents.YOUTUBE_API_URL)
                .client(OkHttpClient.Builder().apply {
                    readTimeout(2, TimeUnit.MINUTES)
                }.build())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            val retrofit = builder.create(RetrofitService::class.java)
            val array = arrayOf(
                "UCUj6rrhMTR9pipbAWBAMvUQ",
                "UCC1LvVTX2ySKYjeIXkAtvsQ",
                "UCewitUbsXnyjvJjGgxa0IYw",
                "UCxQXvvaqwA2NzPXs5775ogw",
                "UCAmff0euQRf6RwVlbB8PLMw",
                "UCgiO7Kxib0SZEG0DoeuBkdQ",
                "UCEkcg9WqCX4sGxRQ3uUkekA"
            )

            val result = retrofit.getYChannelInfo(array)
                .await()
            val channelList = arrayOfNulls<String>(7)
                .toMutableList()

            result.items.forEach {
                val index = array.indexOf(it.id)
                channelList[index] = it.id
            }
            println("res: ${channelList.toList()}")
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
                .baseUrl(Contents.YOUTUBE_API_URL)
                .client(OkHttpClient.Builder().apply {
                    readTimeout(2, TimeUnit.MINUTES)
                }.build())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            val retrofit = builder.create(RetrofitService::class.java)

            val playlistItems = retrofit.getYPlaylistItems(
                "UUUj6rrhMTR9pipbAWBAMvUQ",
                null
            ).await()

            val videosId = playlistItems.items.map { it.contentDetails.videoId }
                .toTypedArray()

            val videosResponse = retrofit.getYVideos(videosId)
                .await()

            println("끝")
            println("res: $videosResponse")
        }
    }
}
