package com.sghore.chimtubeworld.viewmodel.storeDetailFrag

import com.sghore.chimtubeworld.data.Goods
import com.sghore.chimtubeworld.other.Contents
import org.jsoup.Jsoup
import javax.inject.Inject

class StoreDetailRepository @Inject constructor(

) {
    // 굿즈에 프리뷰 이미지 리스트들을 가져옴
    fun getStoreDetail(goods: Goods): List<String> {
        val images = mutableListOf(goods.thumbnailImage)
        val doc = Jsoup.connect(goods.url)
            .userAgent("19.0.1.84.52")
            .get()

        if (goods.type == Contents.STORE_MUCH_MERCH_URL) {
            val itemList = doc.select("div.xans-element-")
                .select("li.xans-record- img")

            for ((index, element) in itemList.withIndex()) {
                if (index == 0) {
                    continue
                }

                val image = "https:" + element.attr("src")
                images.add(image)
            }
        } else {
            val itemList = doc.select("li._3TvRO_uxKp")
                .select("img")

            for ((index, element) in itemList.withIndex()) {
                if (index == 0) {
                    continue
                }

                val image = element.attr("src").replace("f40", "m510")
                images.add(image)
            }
        }

        return images.toList()
    }
}