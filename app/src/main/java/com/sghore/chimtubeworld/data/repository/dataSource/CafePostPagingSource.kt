package com.sghore.chimtubeworld.data.repository.dataSource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.sghore.chimtubeworld.data.model.Post
import com.sghore.chimtubeworld.data.db.Dao
import com.sghore.chimtubeworld.data.retrofit.NaverRetrofitService
import com.sghore.chimtubeworld.other.Constants
import org.jsoup.Connection
import org.jsoup.Jsoup
import java.text.SimpleDateFormat
import java.util.Locale

class CafePostPagingSource(
    private val categoryId: Int,
    private val retrofitService: NaverRetrofitService,
    private val dao: Dao
) :
    PagingSource<Int, Post>() {

    override fun getRefreshKey(state: PagingState<Int, Post>): Int {
        return 1
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Post> {
        return try {
            val pageKey = params.key ?: 1
            val posts = retrofitService.getCafePosts(categoryId, pageKey)

            if (posts.result.articleList.isEmpty())
                throw NullPointerException()

            val postList = posts.result.articleList.map {
                val isRead = dao.getReadData(it.item.articleId).isNotEmpty() // 읽음 여부

                Post(
                    id = it.item.articleId,
                    title = if (it.item.headName != null) {
                        "[${it.item.headName}] "
                    } else {
                        ""
                    } + it.item.subject,
                    userName = it.item.writerInfo.nickName,
                    postDate = SimpleDateFormat(
                        "yyyy.MM.dd.",
                        Locale.KOREA
                    ).format(it.item.writeDateTimestamp),
                    postImage = it.item.representImage ?: "",
                    url = Constants.CAFE_MAIN_URL + "/${it.item.articleId}",
                    isRead = isRead
                )
            }

            LoadResult.Page(
                data = postList,
                prevKey = null,
                nextKey = pageKey + 1
            )
        } catch (e: Exception) {
            e.printStackTrace()
            LoadResult.Error(e)
        }
    }

    // Jsoup을 통해 카페글의 HTML을 가져옴
    private fun connectJsoup(url: String) =
        Jsoup.connect(url)
            .userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/115.0.0.0 Safari/537.36")
            .header(
                "accept",
                "accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9"
            )
            .header("accept-encoding", "gzip, deflate, br")
            .header("accept-language", "ko-KR,ko;q=0.9")
            .method(Connection.Method.GET)
            .get()
}