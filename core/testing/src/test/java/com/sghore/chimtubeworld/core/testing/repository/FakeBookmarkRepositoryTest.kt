package com.sghore.chimtubeworld.core.testing.repository

import com.sghore.chimtubeworld.core.testing.model.bookmark
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FakeBookmarkRepositoryTest {

    @Test
    fun `북마크 추가 실패를 기록하고 결과를 바꾸면 재시도한다`() = runTest {
        val item = bookmark(id = 7)
        val repository = FakeBookmarkRepository()
        val failure = IllegalStateException("조회 실패")
        repository.setAddError(failure)

        assertSame(failure, runCatching { repository.addBookmark(item) }.exceptionOrNull())
        assertEquals(listOf(item), repository.addRequests)

        repository.setAddResult()
        repository.addBookmark(item)

        assertEquals(listOf(item, item), repository.addRequests)
    }

    @Test
    fun `북마크 추가 응답과 지연은 호출 시작 시 고정된다`() = runTest {
        val item = bookmark(id = 7)
        val repository = FakeBookmarkRepository()
        repository.setAddResult()
        repository.addResponseDelayMillis = 100L
        val response = async { repository.addBookmark(item) }
        runCurrent()

        assertEquals(listOf(item), repository.addRequests)
        val failure = IllegalStateException("다음 호출 실패")
        repository.setAddError(failure)
        repository.addResponseDelayMillis = 0L
        advanceTimeBy(99L)
        runCurrent()
        assertFalse(response.isCompleted)

        advanceTimeBy(1L)
        runCurrent()
        response.await()
        assertTrue(response.isCompleted)
        assertSame(failure, runCatching { repository.addBookmark(item) }.exceptionOrNull())
    }

    @Test
    fun `북마크 삭제 실패를 기록하고 결과를 바꾸면 재시도한다`() = runTest {
        val item = bookmark(id = 7)
        val repository = FakeBookmarkRepository()
        val failure = IllegalStateException("조회 실패")
        repository.setDeleteError(failure)

        assertSame(failure, runCatching { repository.deleteBookmark(item) }.exceptionOrNull())
        assertEquals(listOf(item), repository.deleteRequests)

        repository.setDeleteResult()
        repository.deleteBookmark(item)

        assertEquals(listOf(item, item), repository.deleteRequests)
    }

    @Test
    fun `북마크 삭제 응답과 지연은 호출 시작 시 고정된다`() = runTest {
        val item = bookmark(id = 7)
        val repository = FakeBookmarkRepository()
        repository.setDeleteResult()
        repository.deleteResponseDelayMillis = 100L
        val response = async { repository.deleteBookmark(item) }
        runCurrent()

        assertEquals(listOf(item), repository.deleteRequests)
        val failure = IllegalStateException("다음 호출 실패")
        repository.setDeleteError(failure)
        repository.deleteResponseDelayMillis = 0L
        advanceTimeBy(99L)
        runCurrent()
        assertFalse(response.isCompleted)

        advanceTimeBy(1L)
        runCurrent()
        response.await()
        assertTrue(response.isCompleted)
        assertSame(failure, runCatching { repository.deleteBookmark(item) }.exceptionOrNull())
    }

    @Test
    fun `북마크 ID 조회 실패를 기록하고 결과를 바꾸면 재시도한다`() = runTest {
        val item = bookmark(id = 7)
        val repository = FakeBookmarkRepository()
        val failure = IllegalStateException("조회 실패")
        repository.setItemIdError(failure)

        assertSame(failure, runCatching { repository.getItemId(item) }.exceptionOrNull())
        assertEquals(listOf(item), repository.itemIdRequests)

        val recovered = 42
        repository.setItemIdResult(recovered)

        assertEquals(recovered, repository.getItemId(item))
    }

    @Test
    fun `북마크 ID 조회 응답과 지연은 호출 시작 시 고정된다`() = runTest {
        val item = bookmark(id = 7)
        val repository = FakeBookmarkRepository()
        val initial = 42
        repository.setItemIdResult(initial)
        repository.itemIdResponseDelayMillis = 100L
        val response = async { repository.getItemId(item) }
        runCurrent()

        assertEquals(listOf(item), repository.itemIdRequests)
        val next = 73
        repository.setItemIdResult(next)
        repository.itemIdResponseDelayMillis = 0L
        advanceTimeBy(99L)
        runCurrent()
        assertFalse(response.isCompleted)

        advanceTimeBy(1L)
        runCurrent()
        assertEquals(initial, response.await())
        assertEquals(next, repository.getItemId(item))
    }

    @Test
    fun `음수 응답 지연은 허용하지 않는다`() {
        val repository = FakeBookmarkRepository()
        assertTrue(runCatching { repository.addResponseDelayMillis = -1L }.exceptionOrNull() is IllegalArgumentException)
        assertTrue(runCatching { repository.deleteResponseDelayMillis = -1L }.exceptionOrNull() is IllegalArgumentException)
        assertTrue(runCatching { repository.itemIdResponseDelayMillis = -1L }.exceptionOrNull() is IllegalArgumentException)
    }

    @Test
    fun `요청은 순서대로 기록하고 다른 메서드와 인스턴스의 설정은 독립적이다`() = runTest {
        val first = FakeBookmarkRepository(initialItemId = 42)
        val second = FakeBookmarkRepository(initialItemId = 73)
        val a = bookmark(id = 7, title = "첫 장면")
        val b = bookmark(id = 8, title = "다음 장면")
        first.addBookmark(a)
        val recorded = first.addRequests
        first.addBookmark(b)
        first.setAddError(IllegalStateException("추가 실패"))

        first.deleteBookmark(b)
        second.addBookmark(b)

        assertSame(a, first.addRequests[0])
        assertSame(b, first.addRequests[1])
        assertEquals(1, recorded.size)
        assertSame(b, first.deleteRequests.single())
        assertSame(b, second.addRequests.single())
        assertEquals(42, first.getItemId(a))
        assertEquals(73, second.getItemId(b))
    }
}
