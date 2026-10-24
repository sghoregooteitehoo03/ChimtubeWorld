package com.sghore.chimtubeworld.core.testing.repository

import androidx.paging.PagingData
import androidx.paging.testing.asSnapshot
import com.sghore.chimtubeworld.core.testing.model.channel
import com.sghore.chimtubeworld.core.testing.model.post
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FakeCafeRepositoryTest {

    @Test
    fun `카페 정보 조회 실패를 기록하고 결과를 바꾸면 재시도한다`() = runTest {
        val repository = FakeCafeRepository()
        val failure = IllegalStateException("조회 실패")
        repository.setInfoError(failure)

        assertSame(failure, runCatching { repository.getCafeInfo() }.exceptionOrNull())
        assertEquals(1, repository.infoCallCount)

        val recovered = channel(id = "first")
        repository.setInfoResult(recovered)

        assertEquals(recovered, repository.getCafeInfo())
    }

    @Test
    fun `카페 정보 조회 응답과 지연은 호출 시작 시 고정된다`() = runTest {
        val repository = FakeCafeRepository()
        val initial = channel(id = "first")
        repository.setInfoResult(initial)
        repository.infoResponseDelayMillis = 100L
        val response = async { repository.getCafeInfo() }
        runCurrent()

        assertEquals(1, repository.infoCallCount)
        val next = channel(id = "next")
        repository.setInfoResult(next)
        repository.infoResponseDelayMillis = 0L
        advanceTimeBy(99L)
        runCurrent()
        assertFalse(response.isCompleted)

        advanceTimeBy(1L)
        runCurrent()
        assertEquals(initial, response.await())
        assertEquals(next, repository.getCafeInfo())
    }

    @Test
    fun `게시글 읽음 처리 실패를 기록하고 결과를 바꾸면 재시도한다`() = runTest {
        val repository = FakeCafeRepository()
        val failure = IllegalStateException("조회 실패")
        repository.setReadError(failure)

        assertSame(failure, runCatching { repository.readPost(42) }.exceptionOrNull())
        assertEquals(listOf(42), repository.readRequests)

        repository.setReadResult()
        repository.readPost(42)

        assertEquals(listOf(42, 42), repository.readRequests)
    }

    @Test
    fun `게시글 읽음 처리 응답과 지연은 호출 시작 시 고정된다`() = runTest {
        val repository = FakeCafeRepository()
        repository.setReadResult()
        repository.readResponseDelayMillis = 100L
        val response = async { repository.readPost(42) }
        runCurrent()

        assertEquals(listOf(42), repository.readRequests)
        val failure = IllegalStateException("다음 호출 실패")
        repository.setReadError(failure)
        repository.readResponseDelayMillis = 0L
        advanceTimeBy(99L)
        runCurrent()
        assertFalse(response.isCompleted)

        advanceTimeBy(1L)
        runCurrent()
        response.await()
        assertTrue(response.isCompleted)
        assertSame(failure, runCatching { repository.readPost(42) }.exceptionOrNull())
    }

    @Test
    fun `게시글 조회는 목록과 빈 응답 및 Flow 응답을 설정하고 인자를 기록한다`() = runTest {
        val repository = FakeCafeRepository()
        val item = post(id = 42)
        repository.setPostsResult(listOf(item))

        assertEquals(listOf(item), repository.getCafePosts(17).asSnapshot())
        assertEquals(listOf(17), repository.postsRequests)

        repository.setPostsResult(emptyList())
        assertTrue(repository.getCafePosts(17).asSnapshot().isEmpty())

        repository.setPostsResult(flowOf(PagingData.from(listOf(item))))
        assertEquals(listOf(item), repository.getCafePosts(17).asSnapshot())
    }

    @Test
    fun `게시글 조회 실패는 수집 시 전달되고 다음 호출은 복구된다`() = runTest {
        val repository = FakeCafeRepository()
        val failure = IllegalStateException("페이징 조회 실패")
        repository.setPostsError(failure)
        val pending = repository.getCafePosts(17)
        repository.setPostsResult(emptyList())

        val actual = runCatching { pending.asSnapshot() }.exceptionOrNull()
        assertTrue(actual is IllegalStateException)
        assertEquals(failure.message, actual?.message)
        assertEquals(listOf(17), repository.postsRequests)
        assertTrue(repository.getCafePosts(17).asSnapshot().isEmpty())
    }

    @Test
    fun `게시글 조회는 수집 전 설정을 바꿔도 호출 당시 응답과 지연을 사용한다`() = runTest {
        val repository = FakeCafeRepository()
        val initial = post(id = 42)
        repository.setPostsResult(listOf(initial))
        repository.postsResponseDelayMillis = 100L
        val pending = repository.getCafePosts(17)

        val next = post(id = 73)
        repository.setPostsResult(listOf(next))
        repository.postsResponseDelayMillis = 0L
        val response = async { pending.asSnapshot() }
        runCurrent()
        advanceTimeBy(99L)
        runCurrent()
        assertFalse(response.isCompleted)

        advanceTimeBy(1L)
        runCurrent()
        assertEquals(listOf(initial), response.await())
        assertEquals(listOf(next), repository.getCafePosts(17).asSnapshot())
    }

    @Test
    fun `음수 응답 지연은 허용하지 않는다`() {
        val repository = FakeCafeRepository()
        assertTrue(runCatching { repository.infoResponseDelayMillis = -1L }.exceptionOrNull() is IllegalArgumentException)
        assertTrue(runCatching { repository.readResponseDelayMillis = -1L }.exceptionOrNull() is IllegalArgumentException)
        assertTrue(runCatching { repository.postsResponseDelayMillis = -1L }.exceptionOrNull() is IllegalArgumentException)
    }

    @Test
    fun `카테고리와 읽음 요청은 순서대로 기록하고 다른 인스턴스와 격리된다`() = runTest {
        val initial = channel()
        val first = FakeCafeRepository()
        val second = FakeCafeRepository(initialInfo = initial)
        first.getCafePosts(17)
        first.getCafePosts(73)
        first.readPost(42)
        first.readPost(51)
        first.setInfoError(IllegalStateException("조회 실패"))

        assertSame(initial, second.getCafeInfo())
        assertEquals(listOf(17, 73), first.postsRequests)
        assertEquals(listOf(42, 51), first.readRequests)
        assertTrue(second.postsRequests.isEmpty())
        assertTrue(second.readRequests.isEmpty())
    }
}
