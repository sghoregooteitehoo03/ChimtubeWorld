package com.sghore.chimtubeworld.viewmodel.storeFrag

import com.google.firebase.firestore.FirebaseFirestore
import com.sghore.chimtubeworld.data.Channel
import com.sghore.chimtubeworld.data.Goods
import com.sghore.chimtubeworld.data.LinkInfo
import com.sghore.chimtubeworld.data.Post
import com.sghore.chimtubeworld.other.Contents
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.tasks.await
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import javax.inject.Inject

class StoreRepository @Inject constructor(
    private val store: FirebaseFirestore
) {

    // 스토어의 이미지를 가져옴
    suspend fun getStoreInfo(): List<Channel> {
        val storeInfoList = mutableListOf<Channel>()
        val documents = store.collection(Contents.COLLECTION_GOODS_LINK)
            .get()
            .await()

        // 스토어의 링크를 가져옴
        documents.forEach { document ->
            val linkInfo = LinkInfo(
                id = document["id"] as String,
                url = document["url"] as String,
                explain = document["explain"] as String
            )
            val image = getStoreImage(
                linkInfo.url,
                linkInfo.id
            )
            val storeInfo = Channel(
                id = "",
                name = linkInfo.explain,
                explains = arrayOf(),
                url = linkInfo.url,
                image = image,
                type = 0
            )

            storeInfoList.add(storeInfo)
        }

        return storeInfoList.toList()
    }

    fun getGoodsList(url: String): List<Goods> {
        val doc = getConnection(url)

        return if (url.startsWith(Contents.STORE_MUCH_MERCH_URL)) {
            getMuchMerchList(doc)
        } else {
            getNaverStoreList(doc)
        }
    }

    // 사이트에 접속하여 스토어 이미지를 읽어옴
    private fun getStoreImage(url: String, storeId: String): String {
        val doc = getConnection(url)
        var imageUrl = doc.select("a")
            .filter { it.attr("href") == storeId }[0]
            .select("img")
            .attr("src")

        if (!imageUrl.startsWith("https://")) {
            imageUrl = StringBuilder(imageUrl).insert(0, Contents.STORE_MUCH_MERCH_URL)
                .toString()
        }

        return imageUrl
    }

    private fun getMuchMerchList(doc: Document): List<Goods> =
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

    private fun getNaverStoreList(doc: Document): List<Goods> =
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

    private fun getConnection(url: String) =
        Jsoup.connect(url)
            .userAgent("19.0.1.84.52")
            .get()
}