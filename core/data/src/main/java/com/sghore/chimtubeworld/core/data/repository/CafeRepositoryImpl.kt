package com.sghore.chimtubeworld.core.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.sghore.chimtubeworld.core.network.datasource.CafeRemoteDataSource
import com.sghore.chimtubeworld.core.data.repository.dataSource.CafePostPagingSource
import com.sghore.chimtubeworld.core.model.Channel
import com.sghore.chimtubeworld.core.database.entity.ReadHistoryEntity
import com.sghore.chimtubeworld.core.database.dao.Dao
import com.sghore.chimtubeworld.core.common.Constants
import com.sghore.chimtubeworld.core.common.di.DispatcherModule.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import org.jsoup.Jsoup
import java.text.DecimalFormat
import com.sghore.chimtubeworld.core.domain.repository.CafeRepository
import javax.inject.Inject

internal class CafeRepositoryImpl @Inject constructor(
    private val dao: Dao,
    private val remoteDataSource: CafeRemoteDataSource,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : CafeRepository {
    override fun getCafePosts(categoryId: Int) =
        Pager(PagingConfig(10)) {
            CafePostPagingSource(
                categoryId = categoryId,
                remoteDataSource = remoteDataSource,
                dao = dao
            )
        }.flow

    // 침착맨 팬카페의 정보를 가져옴
    override suspend fun getCafeInfo(): Channel {
        return CoroutineScope(ioDispatcher).async {
            val doc = Jsoup.connect(Constants.CAFE_MAIN_URL)
                .userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/115.0.0.0 Safari/537.36")
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

            Channel(
                id = "",
                name = cafeTitle,
                explains = arrayOf(DecimalFormat("#,###").format(memberNumbers)),
                url = Constants.CAFE_MAIN_URL,
                image = gmImage,
                type = 0
            )
        }.await()
    }

    override suspend fun readPost(articleId: Int) {
        if (dao.getReadData(articleId).isEmpty()) { // 데이터가 없을 때 insert 작업
            dao.insertReadData(ReadHistoryEntity(articleId = articleId))
        }
    }
}
