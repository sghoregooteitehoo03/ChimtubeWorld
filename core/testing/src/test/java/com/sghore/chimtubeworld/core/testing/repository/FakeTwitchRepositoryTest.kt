package com.sghore.chimtubeworld.core.testing.repository

import com.sghore.chimtubeworld.core.model.Channel
import com.sghore.chimtubeworld.core.testing.model.channel
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
class FakeTwitchRepositoryTest {

    @Test
    fun `채널 조회 실패를 기록하고 결과를 바꾸면 재시도한다`() = runTest {
        val repository = FakeTwitchRepository()
        val failure = IllegalStateException("조회 실패")
        repository.setChannelsError(failure)

        assertSame(failure, runCatching { repository.getChannels() }.exceptionOrNull())
        assertEquals(1, repository.channelsCallCount)

        val recovered = listOf(channel(id = "first"))
        repository.setChannelsResult(recovered)

        assertEquals(recovered, repository.getChannels())
    }

    @Test
    fun `채널 조회 응답과 지연은 호출 시작 시 고정된다`() = runTest {
        val repository = FakeTwitchRepository()
        val initial = listOf(channel(id = "first"))
        repository.setChannelsResult(initial)
        repository.channelsResponseDelayMillis = 100L
        val response = async { repository.getChannels() }
        runCurrent()

        assertEquals(1, repository.channelsCallCount)
        val next = emptyList<Channel>()
        repository.setChannelsResult(next)
        repository.channelsResponseDelayMillis = 0L
        advanceTimeBy(99L)
        runCurrent()
        assertFalse(response.isCompleted)

        advanceTimeBy(1L)
        runCurrent()
        assertEquals(initial, response.await())
        assertEquals(next, repository.getChannels())
    }

    @Test
    fun `음수 응답 지연은 허용하지 않는다`() {
        val repository = FakeTwitchRepository()
        assertTrue(runCatching { repository.channelsResponseDelayMillis = -1L }.exceptionOrNull() is IllegalArgumentException)
    }

    @Test
    fun `취소된 호출도 기록하며 다른 Fake 인스턴스와 격리된다`() = runTest {
        val first = FakeTwitchRepository().apply { channelsResponseDelayMillis = 1_000L }
        val second = FakeTwitchRepository()
        val response = async { first.getChannels() }
        runCurrent()
        response.cancel()
        response.join()

        assertTrue(response.isCancelled)
        first.setChannelsError(IllegalStateException("실패"))
        assertTrue(second.getChannels().isEmpty())
        assertEquals(1, first.channelsCallCount)
        assertEquals(1, second.channelsCallCount)

        first.channelsResponseDelayMillis = 0L
        first.setChannelsResult(emptyList())
        assertTrue(first.getChannels().isEmpty())
        assertEquals(2, first.channelsCallCount)
    }
}
