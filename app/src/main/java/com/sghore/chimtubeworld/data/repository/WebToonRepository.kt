package com.sghore.chimtubeworld.data.repository

import android.graphics.Color
import com.sghore.chimtubeworld.data.model.Channel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import org.jsoup.Jsoup
import javax.inject.Inject

class WebToonRepository @Inject constructor(

) {

    suspend fun getWebToonList(): List<Channel> {
        val colorList = listOf(
            Color.parseColor("#C3B9A0"),
            Color.parseColor("#CA7466"),
            Color.parseColor("#64B6D1"),
            Color.parseColor("#8BBD8E"),
            Color.parseColor("#7A9937")
        ) // 배경색 리스트
        val mainUrl = "https://comic.naver.com" // 네이버 웹툰 url
        // 이말년의 웹툰을 검색한 url
        val searchUrl =
            "${mainUrl}/search?m=webtoon&keyword=%EC%9D%B4%EB%A7%90%EB%85%84&type=artist&page=1"

        val webToonList = CoroutineScope(Dispatchers.IO).async {
            val listDoc = Jsoup.connect(searchUrl)
                .userAgent("19.0.1.84.52")
                .get()

            // 이말년의 웹툰들을 읽어옴
            val elements = listDoc.select("ul.resultList")
                .select("li h5 a")

            // 웹툰의 썸네일을 가져오는 작업
            elements.mapIndexed { index, element ->
                val nextUrl = element.attr("href")
                val imageDoc = Jsoup.connect(mainUrl + nextUrl)
                    .userAgent("19.0.1.84.52")
                    .get()

                // 웹툰 아이디
                val id = nextUrl.split("=")[1]
                // 웹툰 썸네일
                val image = imageDoc.select("div.comicinfo")
                    .select("img")
                    .attr("src")
                // 웹툰 제목
                val title = imageDoc.select("div.comicinfo")
                    .select("img")
                    .attr("title")
                // 웹툰 최근 업데이트 날짜
                val lastUpdate = imageDoc.select("table.viewList")
                    .select("tbody tr td.num")[1]
                    .text()

                Channel(
                    id = id,
                    name = title,
                    explains = arrayOf("최종업데이트: $lastUpdate"),
                    url = mainUrl + nextUrl,
                    image = image,
                    type = colorList[index]
                )
            }
        }.await()

        return webToonList
    }
}