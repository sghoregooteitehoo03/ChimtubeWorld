package com.sghore.chimtubeworld.core.testing.repository

import androidx.paging.PagingData
import androidx.paging.testing.asSnapshot
import com.sghore.chimtubeworld.core.model.Channel
import com.sghore.chimtubeworld.core.testing.model.channel
import com.sghore.chimtubeworld.core.testing.model.playlist
import com.sghore.chimtubeworld.core.testing.model.video
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
class FakeYoutubeRepositoryTest {

    @Test
    fun `채널 조회 실패를 기록하고 결과를 바꾸면 재시도한다`() = runTest {
        val repository = FakeYoutubeRepository()
        val failure = IllegalStateException("조회 실패")
        repository.setChannelsError(failure)

        assertSame(failure, runCatching { repository.getChannels() }.exceptionOrNull())
        assertEquals(1, repository.channelsCallCount)

        val recovered = listOf(channel(), null)
        repository.setChannelsResult(recovered)

        assertEquals(recovered, repository.getChannels())
    }

    @Test
    fun `채널 조회 응답과 지연은 호출 시작 시 고정된다`() = runTest {
        val repository = FakeYoutubeRepository()
        val initial = listOf(channel(), null)
        repository.setChannelsResult(initial)
        repository.channelsResponseDelayMillis = 100L
        val response = async { repository.getChannels() }
        runCurrent()

        assertEquals(1, repository.channelsCallCount)
        val next = emptyList<Channel?>()
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
    fun `단일 영상 조회 실패를 기록하고 결과를 바꾸면 재시도한다`() = runTest {
        val repository = FakeYoutubeRepository()
        val failure = IllegalStateException("조회 실패")
        repository.setVideoError(failure)

        assertSame(failure, runCatching { repository.getVideo("requested-url") }.exceptionOrNull())
        assertEquals(listOf("requested-url"), repository.videoRequests)

        val recovered = video(id = "response-id")
        repository.setVideoResult(recovered)

        assertEquals(recovered, repository.getVideo("requested-url"))
    }

    @Test
    fun `단일 영상 조회 응답과 지연은 호출 시작 시 고정된다`() = runTest {
        val repository = FakeYoutubeRepository()
        val initial = video(id = "response-id")
        repository.setVideoResult(initial)
        repository.videoResponseDelayMillis = 100L
        val response = async { repository.getVideo("requested-url") }
        runCurrent()

        assertEquals(listOf("requested-url"), repository.videoRequests)
        val next = video(id = "next")
        repository.setVideoResult(next)
        repository.videoResponseDelayMillis = 0L
        advanceTimeBy(99L)
        runCurrent()
        assertFalse(response.isCompleted)

        advanceTimeBy(1L)
        runCurrent()
        assertEquals(initial, response.await())
        assertEquals(next, repository.getVideo("requested-url"))
    }

    @Test
    fun `영상 목록 조회는 목록과 빈 응답 및 Flow 응답을 설정하고 인자를 기록한다`() = runTest {
        val repository = FakeYoutubeRepository()
        val item = video(id = "response-42")
        repository.setVideosResult(listOf(item))

        assertEquals(listOf(item), repository.getVideos("channel-7").asSnapshot())
        assertEquals(listOf("channel-7"), repository.videosRequests)

        repository.setVideosResult(emptyList())
        assertTrue(repository.getVideos("channel-7").asSnapshot().isEmpty())

        repository.setVideosResult(flowOf(PagingData.from(listOf(item))))
        assertEquals(listOf(item), repository.getVideos("channel-7").asSnapshot())
    }

    @Test
    fun `영상 목록 조회 실패는 수집 시 전달되고 다음 호출은 복구된다`() = runTest {
        val repository = FakeYoutubeRepository()
        val failure = IllegalStateException("페이징 조회 실패")
        repository.setVideosError(failure)
        val pending = repository.getVideos("channel-7")
        repository.setVideosResult(emptyList())

        val actual = runCatching { pending.asSnapshot() }.exceptionOrNull()
        assertTrue(actual is IllegalStateException)
        assertEquals(failure.message, actual?.message)
        assertEquals(listOf("channel-7"), repository.videosRequests)
        assertTrue(repository.getVideos("channel-7").asSnapshot().isEmpty())
    }

    @Test
    fun `영상 목록 조회는 수집 전 설정을 바꿔도 호출 당시 응답과 지연을 사용한다`() = runTest {
        val repository = FakeYoutubeRepository()
        val initial = video(id = "response-42")
        repository.setVideosResult(listOf(initial))
        repository.videosResponseDelayMillis = 100L
        val pending = repository.getVideos("channel-7")

        val next = video(id = "response-73")
        repository.setVideosResult(listOf(next))
        repository.videosResponseDelayMillis = 0L
        val response = async { pending.asSnapshot() }
        runCurrent()
        advanceTimeBy(99L)
        runCurrent()
        assertFalse(response.isCompleted)

        advanceTimeBy(1L)
        runCurrent()
        assertEquals(listOf(initial), response.await())
        assertEquals(listOf(next), repository.getVideos("channel-7").asSnapshot())
    }

    @Test
    fun `메인 재생목록 조회는 목록과 빈 응답 및 Flow 응답을 설정하고 인자를 기록한다`() = runTest {
        val repository = FakeYoutubeRepository()
        val item = playlist(id = "response-42")
        repository.setMainPlaylistResult(listOf(item))

        assertEquals(listOf(item), repository.getPagingMainPlaylist(null, listOf("requested-7")).asSnapshot())
        assertEquals(listOf(FakeYoutubeRepository.MainPlaylistRequest(null, listOf("requested-7"))), repository.mainPlaylistRequests)

        repository.setMainPlaylistResult(emptyList())
        assertTrue(repository.getPagingMainPlaylist(null, listOf("requested-7")).asSnapshot().isEmpty())

        repository.setMainPlaylistResult(flowOf(PagingData.from(listOf(item))))
        assertEquals(listOf(item), repository.getPagingMainPlaylist(null, listOf("requested-7")).asSnapshot())
    }

    @Test
    fun `메인 재생목록 조회 실패는 수집 시 전달되고 다음 호출은 복구된다`() = runTest {
        val repository = FakeYoutubeRepository()
        val failure = IllegalStateException("페이징 조회 실패")
        repository.setMainPlaylistError(failure)
        val pending = repository.getPagingMainPlaylist(null, listOf("requested-7"))
        repository.setMainPlaylistResult(emptyList())

        val actual = runCatching { pending.asSnapshot() }.exceptionOrNull()
        assertTrue(actual is IllegalStateException)
        assertEquals(failure.message, actual?.message)
        assertEquals(listOf(FakeYoutubeRepository.MainPlaylistRequest(null, listOf("requested-7"))), repository.mainPlaylistRequests)
        assertTrue(repository.getPagingMainPlaylist(null, listOf("requested-7")).asSnapshot().isEmpty())
    }

    @Test
    fun `메인 재생목록 조회는 수집 전 설정을 바꿔도 호출 당시 응답과 지연을 사용한다`() = runTest {
        val repository = FakeYoutubeRepository()
        val initial = playlist(id = "response-42")
        repository.setMainPlaylistResult(listOf(initial))
        repository.mainPlaylistResponseDelayMillis = 100L
        val pending = repository.getPagingMainPlaylist(null, listOf("requested-7"))

        val next = playlist(id = "response-73")
        repository.setMainPlaylistResult(listOf(next))
        repository.mainPlaylistResponseDelayMillis = 0L
        val response = async { pending.asSnapshot() }
        runCurrent()
        advanceTimeBy(99L)
        runCurrent()
        assertFalse(response.isCompleted)

        advanceTimeBy(1L)
        runCurrent()
        assertEquals(listOf(initial), response.await())
        assertEquals(listOf(next), repository.getPagingMainPlaylist(null, listOf("requested-7")).asSnapshot())
    }

    @Test
    fun `서브 재생목록 조회는 목록과 빈 응답 및 Flow 응답을 설정하고 인자를 기록한다`() = runTest {
        val repository = FakeYoutubeRepository()
        val item = playlist(id = "response-42")
        repository.setSubPlaylistResult(listOf(item))

        assertEquals(listOf(item), repository.getPagingSubPlaylist(listOf("requested-7")).asSnapshot())
        assertEquals(listOf(listOf("requested-7")), repository.subPlaylistRequests)

        repository.setSubPlaylistResult(emptyList())
        assertTrue(repository.getPagingSubPlaylist(listOf("requested-7")).asSnapshot().isEmpty())

        repository.setSubPlaylistResult(flowOf(PagingData.from(listOf(item))))
        assertEquals(listOf(item), repository.getPagingSubPlaylist(listOf("requested-7")).asSnapshot())
    }

    @Test
    fun `서브 재생목록 조회 실패는 수집 시 전달되고 다음 호출은 복구된다`() = runTest {
        val repository = FakeYoutubeRepository()
        val failure = IllegalStateException("페이징 조회 실패")
        repository.setSubPlaylistError(failure)
        val pending = repository.getPagingSubPlaylist(listOf("requested-7"))
        repository.setSubPlaylistResult(emptyList())

        val actual = runCatching { pending.asSnapshot() }.exceptionOrNull()
        assertTrue(actual is IllegalStateException)
        assertEquals(failure.message, actual?.message)
        assertEquals(listOf(listOf("requested-7")), repository.subPlaylistRequests)
        assertTrue(repository.getPagingSubPlaylist(listOf("requested-7")).asSnapshot().isEmpty())
    }

    @Test
    fun `서브 재생목록 조회는 수집 전 설정을 바꿔도 호출 당시 응답과 지연을 사용한다`() = runTest {
        val repository = FakeYoutubeRepository()
        val initial = playlist(id = "response-42")
        repository.setSubPlaylistResult(listOf(initial))
        repository.subPlaylistResponseDelayMillis = 100L
        val pending = repository.getPagingSubPlaylist(listOf("requested-7"))

        val next = playlist(id = "response-73")
        repository.setSubPlaylistResult(listOf(next))
        repository.subPlaylistResponseDelayMillis = 0L
        val response = async { pending.asSnapshot() }
        runCurrent()
        advanceTimeBy(99L)
        runCurrent()
        assertFalse(response.isCompleted)

        advanceTimeBy(1L)
        runCurrent()
        assertEquals(listOf(initial), response.await())
        assertEquals(listOf(next), repository.getPagingSubPlaylist(listOf("requested-7")).asSnapshot())
    }

    @Test
    fun `음수 응답 지연은 허용하지 않는다`() {
        val repository = FakeYoutubeRepository()
        assertTrue(runCatching { repository.channelsResponseDelayMillis = -1L }.exceptionOrNull() is IllegalArgumentException)
        assertTrue(runCatching { repository.videoResponseDelayMillis = -1L }.exceptionOrNull() is IllegalArgumentException)
        assertTrue(runCatching { repository.videosResponseDelayMillis = -1L }.exceptionOrNull() is IllegalArgumentException)
        assertTrue(runCatching { repository.mainPlaylistResponseDelayMillis = -1L }.exceptionOrNull() is IllegalArgumentException)
        assertTrue(runCatching { repository.subPlaylistResponseDelayMillis = -1L }.exceptionOrNull() is IllegalArgumentException)
    }

    @Test
    fun `재생목록 인자와 요청 기록의 목록은 외부 변경에서 격리된다`() {
        val repository = FakeYoutubeRepository()
        val ids = mutableListOf("first", "second")
        repository.getPagingMainPlaylist("channel", ids)
        repository.getPagingSubPlaylist(ids)
        val recorded = repository.mainPlaylistRequests
        ids.clear()
        repository.getPagingMainPlaylist("other", emptyList())

        assertEquals(listOf("first", "second"), recorded.single().playlistId)
        assertEquals("channel", recorded.single().channelId)
        assertEquals(listOf(listOf("first", "second")), repository.subPlaylistRequests)
        assertEquals(1, recorded.size)
        assertEquals(2, repository.mainPlaylistRequests.size)
    }

    @Test
    fun `페이징 수집을 취소해도 다음 호출과 다른 인스턴스는 독립적이다`() = runTest {
        val first = FakeYoutubeRepository().apply { videosResponseDelayMillis = 1_000L }
        val second = FakeYoutubeRepository()
        val response = async { first.getVideos("cancelled").asSnapshot() }
        runCurrent()
        response.cancel()
        response.join()

        assertTrue(response.isCancelled)
        first.setVideosError(IllegalStateException("실패"))
        assertTrue(second.getVideos("second").asSnapshot().isEmpty())
        first.setVideosResult(emptyList())
        first.videosResponseDelayMillis = 0L
        assertTrue(first.getVideos("retry").asSnapshot().isEmpty())
        assertEquals(listOf("cancelled", "retry"), first.videosRequests)
        assertEquals(listOf("second"), second.videosRequests)
    }
}
