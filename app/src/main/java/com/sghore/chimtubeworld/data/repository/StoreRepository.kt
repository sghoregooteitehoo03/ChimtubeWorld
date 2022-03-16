package com.sghore.chimtubeworld.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.sghore.chimtubeworld.data.model.Channel
import com.sghore.chimtubeworld.data.model.Goods
import com.sghore.chimtubeworld.data.model.LinkInfo
import com.sghore.chimtubeworld.other.Contents
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.tasks.await
import org.jsoup.Jsoup
import javax.inject.Inject

class StoreRepository @Inject constructor(
    private val store: FirebaseFirestore
) {

    // firebase에 저장된 굿즈 스토어에 링크를 가져옴
    suspend fun getStoreLinkData() =
        store.collection(Contents.COLLECTION_GOODS_LINK)
            .get()
            .await()
            .map {
                LinkInfo(
                    id = it["id"] as String,
                    url = it["url"] as String,
                    explain = it["explain"] as String
                )
            }

    // 굿즈 스토어의 링크를 통해 이미지와 이름을 가져옴
    suspend fun getStoreInfo(storeLinkList: List<LinkInfo>): List<Channel> {
        // 데이터의 형식을 바꿈
        return storeLinkList.map { linkInfo ->
            // 스토어의 이미지를 가져옴
            val image = getStoreImage(
                linkInfo.id.split("|")[0], // 이미지를 읽어올 url
                linkInfo.id.split("|")[1] // 이미지의 ID
            )

            Channel(
                id = "",
                name = linkInfo.explain,
                explains = arrayOf(),
                url = linkInfo.url,
                image = image,
                type = 0
            )
        }
    }

    suspend fun getGoodsList(url: String): List<Goods> =
        if (url.startsWith(Contents.STORE_MUCH_MERCH_URL)) {
            getMuchMerchList(url = url)
        } else {
            getNaverStoreList(url = url)
        }

    // 굿즈에 프리뷰 이미지 리스트들을 가져옴
    suspend fun getStorePreviewImages(goods: Goods): List<String> {
        return CoroutineScope(Dispatchers.IO).async {
            val doc = getConnection(goods.url)

            if (goods.type == Contents.STORE_MUCH_MERCH_URL) {
                val itemList = doc.select("div.xans-element-")
                    .select("li.xans-record- img")

                itemList.filterIndexed { index, element -> index != 0 }
                    .map { element ->
                        "https:" + element.attr("src")
                    }
                    .toMutableList().apply {
                        this.add(0, goods.thumbnailImage)
                    }
            } else {
                val itemList = doc.select("li._3TvRO_uxKp")
                    .select("img")

                itemList
                    .filterIndexed { index, element -> index != 0 }
                    .map { element ->
                        element.attr("src").replace("f40", "m510")
                    }.toMutableList().apply {
                        this.add(0, goods.thumbnailImage)
                    }
            }
        }.await()
    }

    // 사이트에 접속하여 스토어 이미지를 읽어옴
    private suspend fun getStoreImage(url: String, storeId: String): String {
        return CoroutineScope(Dispatchers.IO).async {
            val doc = getConnection(url)

            if (url.startsWith(Contents.STORE_MUCH_MERCH_URL)) { // 머치머치
                Contents.STORE_MUCH_MERCH_URL + doc.select("div.brand_box")
                    .filter { it.select("div.brand_data").eachText().contains(storeId) }[0]
                    .select("img")
                    .attr("src")
            } else { // 네이버
                doc.select("div._36jXoyswJd")
                    .select("img")
                    .attr("src")
            }
        }.await()
    }

    private suspend fun getMuchMerchList(url: String): List<Goods> {
        return CoroutineScope(Dispatchers.IO).async {
            val doc = getConnection(url)

            doc.select("ul.prdList")
                .select("li.xans-record-")
                .filter { it.attr("id").isNotEmpty() }
                .map { element ->
                    val goodsName = element.select("div.description span")[2]
                        .text()
                    val goodsPrice = element.select("li.xans-record-")
                        .filter { it.attr("rel") == "판매가" }[0]
                        .select("span")[1]
                        .text()
                    val thumbnailImage = "https:" + element.select("div.prdImg")
                        .select("img")
                        .filter { it.attr("id").isNotEmpty() }[0]
                        .attr("src")
                    val nextUrl = Contents.STORE_MUCH_MERCH_URL + element.select("div.prdImg")
                        .select("a")
                        .attr("href")

                    Goods(
                        title = goodsName,
                        price = goodsPrice,
                        thumbnailImage = thumbnailImage,
                        url = nextUrl,
                        type = Contents.STORE_MUCH_MERCH_URL
                    )
                }
        }.await()
    }

    private suspend fun getNaverStoreList(url: String): List<Goods> {
        return CoroutineScope(Dispatchers.IO).async {
            val doc = getConnection(url)

            doc.select("div._3ZEeXLwPLs")
                .select("li.-qHwcFXhj0")
                .map { element ->
                    val goodsName = element.select("strong.QNNliuiAk3")
                        .text()
                    val goodsPrice = element.select("div._23DThs7PLJ")
                        .text()
                    val thumbnailImage = element.select("img._25CKxIKjAk")
                        .attr("src")
                    val nextUrl = Contents.STORE_NAVER_URL + element.select("a")
                        .attr("href")

                    Goods(
                        title = goodsName,
                        price = goodsPrice,
                        thumbnailImage = thumbnailImage,
                        url = nextUrl,
                        type = Contents.STORE_NAVER_URL
                    )
                }
        }.await()
    }

    private fun getConnection(url: String) =
        Jsoup.connect(url)
            .userAgent("19.0.1.84.52")
            .get()
}