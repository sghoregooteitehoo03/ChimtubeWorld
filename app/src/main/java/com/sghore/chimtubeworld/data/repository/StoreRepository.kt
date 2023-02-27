package com.sghore.chimtubeworld.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.sghore.chimtubeworld.data.model.Goods
import com.sghore.chimtubeworld.data.model.GoodsElement
import com.sghore.chimtubeworld.other.Contents
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.tasks.await
import org.jsoup.Connection
import org.jsoup.Jsoup
import java.text.DecimalFormat
import javax.inject.Inject

class StoreRepository @Inject constructor(
    private val store: FirebaseFirestore
) {

    // firebase에 저장된 굿즈 스토어의 크롤링 요소
    suspend fun getGoodsElement() =
        store.collection(Contents.COLLECTION_GOODS_ELEMENT)
            .get()
            .await()
            .map {
                GoodsElement(
                    titleQuery = it["titleQuery"] as String,
                    priceQuery = it["priceQuery"] as String,
                    imageQuery = it["imageQuery"] as String,
                    channelUrl = it["channelUrl"] as String,
                    goodsUrlQuery = it["goodsUrlQuery"] as String,
                    type = it["type"] as String,
                    channelImage = it["channelImage"] as String,
                    goodsListQuery = it["goodsListQuery"] as String,
                    channelName = it["channelName"] as String,
                    previewImageQuery = it["previewImageQuery"] as String
                )
            }

    // 굿즈 스토어의 크롤링 요소를 가져와 유용한 정보로 반환
    suspend fun getGoodsList(goodsElement: GoodsElement): List<Goods> {
        return CoroutineScope(Dispatchers.IO).async {
            val doc = getConnection(goodsElement.channelUrl)

            doc.select(goodsElement.goodsListQuery).map {
                val title = it.select(goodsElement.titleQuery).text()
                val price = it.select(goodsElement.priceQuery)
                    .text()
                    .replace("[^0-9 ]".toRegex(), "")
                    .toInt()
                val priceText = DecimalFormat("#,###").format(price) + "원"
                val image = it.select(goodsElement.imageQuery)
                    .attr("src")
                    .split("?")[0]
                val goodsUrl =
                    goodsElement.type + it.select(goodsElement.goodsUrlQuery).attr("href")
                        .replace("..", "")

                Goods(
                    title = title,
                    price = priceText,
                    thumbnailImage = image,
                    url = goodsUrl,
                    previewImageQuery = goodsElement.previewImageQuery
                )
            }
        }.await()
    }

    // 굿즈에 프리뷰 이미지 리스트들을 가져옴
    suspend fun getStorePreviewImages(goods: Goods): List<String> {
        return CoroutineScope(Dispatchers.IO).async {
            val doc = getConnection(goods.url)

            doc.select(goods.previewImageQuery)
                .map { it.attr("src").split("?")[0] }
                .toList()
        }.await()
    }

    private fun getConnection(url: String) =
        Jsoup.connect(url)
            .userAgent("19.0.1.84.52")
            .method(Connection.Method.GET)
            .get()
}