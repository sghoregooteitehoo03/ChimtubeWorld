package com.sghore.chimtubeworld.data.repository.dataSource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.sghore.chimtubeworld.data.model.Post
import com.sghore.chimtubeworld.data.db.Dao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import org.jsoup.Connection
import org.jsoup.Jsoup

class CafePostPagingSource(
    private val categoryId: Int,
    private val dao: Dao
) :
    PagingSource<Int, Post>() {

    override fun getRefreshKey(state: PagingState<Int, Post>): Int {
        return 1
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Post> {
        return try {
            val pageKey = params.key ?: 1
            val menuId = if (categoryId != -1) {
                "&search.menuid=${categoryId}"
            } else {
                ""
            }
            val mainUrl =
                "https://cafe.naver.com/ArticleList.nhn?search.clubid=29646865&search.boardtype=C&search.page=${pageKey}&userDisplay=10$menuId"
            val subUrl =
                "https://cafe.naver.com/ArticleList.nhn?search.clubid=29646865&search.boardtype=L&search.page=${pageKey}&userDisplay=10$menuId"

            val postList = CoroutineScope(Dispatchers.IO).async {
                val mainDoc = connectJsoup(mainUrl)
                val subDoc = connectJsoup(subUrl)

                val postDocs = mainDoc.select("div#main-area") // 게시글 모음
                    .select("ul.article-movie-sub li")
                val titleDocs = subDoc.select("div.article-board") // 게시글의 제목(말머리 포함) 모음
                    .select("tbody tr")
                    .filter {
                        it.attr("class").isEmpty() && it.select("td.td_article").isNotEmpty()
                    }

                if (postDocs.isEmpty()) {
                    throw NullPointerException()
                }
                postDocs.mapIndexed { index, postDoc ->
                    val imageDoc = postDoc.select("div.movie-img")
                        .select("a")

                    val url = "https://m.cafe.naver.com" + titleDocs[index] // 게시글 url
                        .select("a.article")
                        .attr("href")
                    val id = url.substringAfter("articleid=")
                        .substringBefore("&")
                        .toInt() // 게시글 아이디
                    val title = titleDocs[index] // 제목
                        .select("a.article")
                        .text()
                    val thumbnailImage = imageDoc.select("img") // 이미지
                        .attr("src")
                    val userName = postDoc.select("td.p-nick") // 유저 이름
                        .select("a")
                        .text()
                    val postDate = postDoc.select("div.date_num") // 포스팅 날짜
                        .select("span.date")
                        .text()
                    val isRead = dao.getReadData(id).isNotEmpty() // 읽음 여부

                    Post(
                        id = id,
                        title = title,
                        userName = userName,
                        postDate = postDate,
                        postImage = thumbnailImage,
                        url = url,
                        isRead = isRead
                    )
                }
            }.await()

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