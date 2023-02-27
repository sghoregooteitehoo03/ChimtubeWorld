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
import retrofit2.awaitResponse
import retrofit2.converter.gson.GsonConverterFactory
import java.text.DecimalFormat
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
        runBlocking {
            val builder = Retrofit.Builder()
                .baseUrl(Contents.YOUTUBE_API_URL)
                .client(OkHttpClient.Builder().apply {
                    readTimeout(2, TimeUnit.MINUTES)
                }.build())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            val retrofit = builder.create(RetrofitService::class.java)

            val result = retrofit.getYPlaylists(
                channelId = null,
                playlistId = listOf(
                    "PLif_jr7pPZABQ2BIQoX_IG4kJ46hUJJJA",
                    "PLif_jr7pPZACAXuOjNHP_FDKuOQ30Gj5l"
                )
            )

            println("result: $result")
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

            val userList = retrofit.getTUserStream(
                "Bearer x1v05hwct1zbc73y19ol89kz0azxdf",
                loginId = arrayOf("rooftopcat99", "nokduro")
            )

            println("끝")
            println("res: ${userList.data}")
        }
    }

    @Test
    fun crawlingTest() {
//        val mainUrl =
//            "https://m.memez.kr/channel/product.php?mode=data&cateCd=002001016&cateMode=goods&page=1&pageNum=40"
//        val response1 = Jsoup.connect(mainUrl)
//            .userAgent("19.0.1.84.52")
//            .method(Connection.Method.GET)
//            .get()
//        val list = response1.select("li.goods_prd_item11")
//        list.forEach { it ->
//            val image = it.select("div.img img")
//                .attr("src")
//                .split("?")[0]
//            val title = it.select("li.prd_name").text()
//            val price = it.select("li.price").text().replace("[^0-9 ]".toRegex(), "")
//            val goodsUrl = "https://m.memez.kr" + it.select("div.goods_list_info a").attr("href")
//                .replace("..", "")
//
//            println("image: $image")
//            println("title: $title")
//            println("price: $price")
//            println("goodsUrl: $goodsUrl")
//            val response2 = Jsoup.connect(goodsUrl)
//                .userAgent("19.0.1.84.52")
//                .method(Connection.Method.GET)
//                .get()
//            val previewImages = response2.select("div.goods_view div.cont_detail img")
//                .map { it.attr("src").split("?")[0] }
//                .toList()
//            println("images: $previewImages")
//        }
        val mainUrl = "https://smartstore.naver.com/uldd"
        val response1 = Jsoup.connect(mainUrl)
            .userAgent("19.0.1.84.52")
            .method(Connection.Method.GET)
            .get()
        val list = response1.select("li.-qHwcFXhj0")
        list.forEach { it ->
            val image = it.select("img._25CKxIKjAk")
                .attr("src")
                .split("?")[0]
            val title = it.select("strong.QNNliuiAk3").text()
            val priceText = it.select("strong._3a2YHGkedh span.nIAdxeTzhx").text()
                .replace("[^0-9]".toRegex(), "")

            val price = DecimalFormat("#,###").format(priceText) + "원"
            val goodsUrl =
                "https://smartstore.naver.com" + it.select("a._3BkKgDHq3l").attr("href")
                    .replace("..", "")

            println("image: $image")
            println("title: $title")
            println("price: $price")
            println("goodsUrl: $goodsUrl")

            val response2 = Jsoup.connect(goodsUrl)
                .userAgent("19.0.1.84.52")
                .method(Connection.Method.GET)
                .get()
            val previewImages = response2.select("li._30l3Wgz_b8 img")
                .map { it.attr("src").split("?")[0] }
                .toList()

            println("images: $previewImages")
        }
    }

    //    -32399000 -> 00:01
    @Test
    fun test() {
        val test = "109ace"
        val a = test.split("|")
        testLoop(a.toMutableList())
    }

    fun testLoop(list: MutableList<String>) {
        for (i in 0..1) {
            if (list.isNotEmpty()) {
                list.removeAt(0)
            }
        }
        println("Check size: ${list.size}")
    }
}