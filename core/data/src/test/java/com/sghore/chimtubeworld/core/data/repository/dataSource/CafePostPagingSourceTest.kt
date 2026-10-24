package com.sghore.chimtubeworld.core.data.repository.dataSource

import androidx.paging.PagingSource
import com.sghore.chimtubeworld.core.common.Constants
import com.sghore.chimtubeworld.core.data.datasource.FakeCafeRemoteDataSource
import com.sghore.chimtubeworld.core.data.datasource.SpyDao
import com.sghore.chimtubeworld.core.data.model.remotePost
import com.sghore.chimtubeworld.core.database.entity.ReadHistoryEntity
import com.sghore.chimtubeworld.core.model.Post
import com.sghore.chimtubeworld.core.network.dto.naver_cafeAPI.CafePostsDTO
import com.sghore.chimtubeworld.core.network.dto.naver_cafeAPI.ResultDTO
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

class CafePostPagingSourceTest {

    @Test
    fun `첫 페이지는 카테고리와 기본 페이지를 전달하고 게시글 및 읽음 상태를 변환한다`() = runTest {
        val first = remotePost(id = 42)
        val second = remotePost(id = 73, headName = null, representImage = null)
        val remote = FakeCafeRemoteDataSource(CafePostsDTO(ResultDTO(listOf(first, second))))
        val dao = SpyDao().apply { readData = listOf(ReadHistoryEntity(id = 7, articleId = 42)) }
        val source = CafePostPagingSource(categoryId = 17, remoteDataSource = remote, dao = dao)

        val result = source.load(PagingSource.LoadParams.Refresh(null, 10, false))
        assertTrue(result is PagingSource.LoadResult.Page<*, *>)
        val page = result as PagingSource.LoadResult.Page<Int, Post>

        assertEquals(listOf(FakeCafeRemoteDataSource.Request(17, 1, 10)), remote.requests)
        assertEquals(listOf(42, 73), dao.readRequests)
        val expectedDate = Instant.ofEpochMilli(first.item.writeDateTimestamp)
            .atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("yyyy.MM.dd."))
        assertEquals(Post(42, "[공지] 원격 게시글", "원격 작성자", expectedDate,
            "https://example.com/post.jpg", Constants.CAFE_MAIN_URL + "/42", true), page.data[0])
        assertEquals("원격 게시글", page.data[1].title)
        assertEquals("", page.data[1].postImage)
        assertFalse(page.data[1].isRead)
        assertNull(page.prevKey)
        assertEquals(2, page.nextKey)
    }

    @Test
    fun `다음 페이지 키를 요청하고 한 페이지 증가한 키를 반환한다`() = runTest {
        val remote = FakeCafeRemoteDataSource(CafePostsDTO(ResultDTO(listOf(remotePost()))))
        val source = CafePostPagingSource(17, remote, SpyDao())

        val result = source.load(PagingSource.LoadParams.Append(3, 10, false))
        val page = result as PagingSource.LoadResult.Page<Int, Post>

        assertEquals(listOf(FakeCafeRemoteDataSource.Request(17, 3, 10)), remote.requests)
        assertNull(page.prevKey)
        assertEquals(4, page.nextKey)
    }

    @Test
    fun `원격 실패는 DAO 조회 없이 같은 예외의 Error를 반환한다`() = runTest {
        val failure = IllegalStateException("원격 실패")
        val remote = FakeCafeRemoteDataSource().apply { this.failure = failure }
        val dao = SpyDao()
        val result = CafePostPagingSource(17, remote, dao)
            .load(PagingSource.LoadParams.Refresh(null, 10, false))

        assertSame(failure, (result as PagingSource.LoadResult.Error<*, *>).throwable)
        assertTrue(dao.readRequests.isEmpty())
    }

    @Test
    fun `읽음 조회 실패는 같은 예외의 Error를 반환한다`() = runTest {
        val failure = IllegalStateException("DAO 실패")
        val remote = FakeCafeRemoteDataSource(CafePostsDTO(ResultDTO(listOf(remotePost()))))
        val dao = SpyDao().apply { getReadFailure = failure }
        val result = CafePostPagingSource(17, remote, dao)
            .load(PagingSource.LoadParams.Refresh(null, 10, false))

        assertSame(failure, (result as PagingSource.LoadResult.Error<*, *>).throwable)
        assertEquals(listOf(42), dao.readRequests)
    }

    @Test
    fun `빈 원격 응답은 기존 정책대로 Error를 반환한다`() = runTest {
        val result = CafePostPagingSource(17, FakeCafeRemoteDataSource(), SpyDao())
            .load(PagingSource.LoadParams.Refresh(null, 10, false))

        assertTrue(result is PagingSource.LoadResult.Error<*, *>)
        assertTrue((result as PagingSource.LoadResult.Error<*, *>).throwable is NullPointerException)
    }
}
