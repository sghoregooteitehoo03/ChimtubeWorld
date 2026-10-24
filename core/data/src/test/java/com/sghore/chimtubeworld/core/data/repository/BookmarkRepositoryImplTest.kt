package com.sghore.chimtubeworld.core.data.repository

import com.sghore.chimtubeworld.core.data.datasource.SpyDao
import com.sghore.chimtubeworld.core.database.entity.BookmarkEntity
import com.sghore.chimtubeworld.core.testing.model.bookmark
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

class BookmarkRepositoryImplTest {

    @Test
    fun `추가할 북마크의 모든 필드를 Entity로 변환해 저장한다`() = runTest {
        val item = bookmark(id = null, videoId = "add-video", title = "추가 장면", videoPosition = 456L, color = 123)
        val dao = SpyDao()
        val repository = BookmarkRepositoryImpl(dao)

        repository.addBookmark(item)

        val expected = BookmarkEntity(null, "add-video", "추가 장면", 456L, 123)
        assertEquals(listOf(expected), dao.bookmarkInsertAttempts)
        assertEquals(listOf(expected), dao.savedBookmarks)
    }

    @Test
    fun `삭제할 북마크의 ID와 모든 필드를 Entity로 전달한다`() = runTest {
        val item = bookmark(id = 7, videoId = "delete-video", title = "삭제 장면", videoPosition = 987L, color = 321)
        val dao = SpyDao()
        val repository = BookmarkRepositoryImpl(dao)

        repository.deleteBookmark(item)

        val expected = BookmarkEntity(7, "delete-video", "삭제 장면", 987L, 321)
        assertEquals(listOf(expected), dao.bookmarkDeleteAttempts)
        assertEquals(listOf(expected), dao.deletedBookmarks)
    }

    @Test
    fun `ID 조회는 북마크 내용으로 요청하고 DAO 응답 ID를 반환한다`() = runTest {
        val item = bookmark(id = 7, videoId = "query-video", title = "찾을 장면", videoPosition = 789L, color = 654)
        val dao = SpyDao().apply { itemId = 42 }
        val repository = BookmarkRepositoryImpl(dao)

        assertEquals(42, repository.getItemId(item))
        assertEquals(listOf(SpyDao.ItemIdRequest("query-video", "찾을 장면", 789L, 654)), dao.itemIdRequests)
        assertTrue(dao.bookmarkInsertAttempts.isEmpty())
        assertTrue(dao.bookmarkDeleteAttempts.isEmpty())
    }

    @Test
    fun `저장 실패는 시도한 Entity를 기록하고 성공한 저장 없이 전파한다`() = runTest {
        val failure = IllegalStateException("저장 실패")
        val dao = SpyDao().apply { insertBookmarkFailure = failure }
        val repository = BookmarkRepositoryImpl(dao)

        assertSame(failure, runCatching { repository.addBookmark(bookmark()) }.exceptionOrNull())
        assertEquals(listOf(BookmarkEntity(1, "video-1", "다시 볼 장면", 12_000L, 0x336699)), dao.bookmarkInsertAttempts)
        assertTrue(dao.savedBookmarks.isEmpty())
    }

    @Test
    fun `삭제 실패는 시도한 Entity를 기록하고 성공한 삭제 없이 전파한다`() = runTest {
        val failure = IllegalStateException("삭제 실패")
        val dao = SpyDao().apply { deleteBookmarkFailure = failure }
        val repository = BookmarkRepositoryImpl(dao)

        assertSame(failure, runCatching { repository.deleteBookmark(bookmark()) }.exceptionOrNull())
        assertEquals(listOf(BookmarkEntity(1, "video-1", "다시 볼 장면", 12_000L, 0x336699)), dao.bookmarkDeleteAttempts)
        assertTrue(dao.deletedBookmarks.isEmpty())
    }

    @Test
    fun `ID 조회 실패는 인자를 기록하고 예외를 전파한다`() = runTest {
        val failure = IllegalStateException("조회 실패")
        val dao = SpyDao().apply { getItemIdFailure = failure }
        val repository = BookmarkRepositoryImpl(dao)

        assertSame(failure, runCatching { repository.getItemId(bookmark()) }.exceptionOrNull())
        assertEquals(listOf(SpyDao.ItemIdRequest("video-1", "다시 볼 장면", 12_000L, 0x336699)), dao.itemIdRequests)
        assertTrue(dao.bookmarkInsertAttempts.isEmpty())
        assertTrue(dao.bookmarkDeleteAttempts.isEmpty())
    }
}
