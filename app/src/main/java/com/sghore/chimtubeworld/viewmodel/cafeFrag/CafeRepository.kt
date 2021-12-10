package com.sghore.chimtubeworld.viewmodel.cafeFrag

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.sghore.chimtubeworld.adapter.paging.CafePostPagingSource
import com.sghore.chimtubeworld.data.Channel
import com.sghore.chimtubeworld.other.Contents
import org.jsoup.Jsoup
import java.text.DecimalFormat
import javax.inject.Inject

class CafeRepository @Inject constructor(

) {
    fun getCafePosts() =
        Pager(PagingConfig(10)) {
            CafePostPagingSource()
        }.flow

    // 침착맨 팬카페의 정보를 가져옴
    fun getCafeInfo(): Channel {
        val doc = Jsoup.connect(Contents.CAFE_MAIN_URL)
            .userAgent("19.0.1.84.52")
            .get()

        // 카페 운영진의 이미지
        val gmImage = doc.select("li.gm-tcol-c")
            .select("a img")
            .attr("src")

        // 카페 회원 수
        val memberNumbers = doc.select("li.mem-cnt-info")
            .select("a em")
            .text()
            .toInt()

        // 카페 이름
        val cafeTitle = doc.select("footer.footer")
            .select("h2.cafe_name")
            .text()

        return Channel(
            name = cafeTitle,
            explains = arrayOf(DecimalFormat("#,###").format(memberNumbers)),
            url = Contents.CAFE_MAIN_URL,
            image = gmImage,
            type = 0
        )
    }
}