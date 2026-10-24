package com.sghore.chimtubeworld.feature.youtube.impl.youtube_screen

import androidx.lifecycle.ViewModelStore
import com.sghore.chimtubeworld.core.testing.model.channel
import com.sghore.chimtubeworld.core.testing.repository.FakeYoutubeRepository
import com.sghore.chimtubeworld.core.testing.rule.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class YoutubeViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val store = ViewModelStore()

    @After
    fun tearDown() {
        store.clear()
    }

    @Test
    fun `초기 로딩 후 채널 목록을 유지하고 클릭 효과를 전달한다`() = runTest(mainDispatcherRule.testDispatcher) {
        val channels = listOf(channel(id = "main"), null, channel(id = "sub", type = 1))
        val repository = FakeYoutubeRepository(initialChannels = channels).apply { channelsResponseDelayMillis = 1000 }
        val vm = YoutubeViewModel(repository).also { store.put("youtube", it) }
        val effects = mutableListOf<YoutubeUiSideEffect>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { vm.sideEffect.collect { effects += it } }
        runCurrent()
        assertTrue(vm.uiState.value.isLoading)
        assertEquals(1, repository.channelsCallCount)
        advanceUntilIdle()
        assertFalse(vm.uiState.value.isLoading)
        assertFalse(vm.uiState.value.hasLoadError)
        assertEquals(channels, vm.uiState.value.channels)
        assertTrue(effects.isEmpty())
        vm.onEvent(YoutubeUiEvent.ChannelClicked(channels[2]!!))
        runCurrent()
        assertEquals(listOf(YoutubeUiSideEffect.NavigateToPlaylists(channels[2]!!)), effects)
        assertEquals(channels, vm.uiState.value.channels)
    }

    @Test
    fun `빈 응답은 정상 상태이고 실패는 의미 있는 오류 상태다`() = runTest(mainDispatcherRule.testDispatcher) {
        val empty = YoutubeViewModel(FakeYoutubeRepository()).also { store.put("empty", it) }
        val failed = YoutubeViewModel(FakeYoutubeRepository().apply { setChannelsError(IllegalStateException("failure")) })
            .also { store.put("failed", it) }
        advanceUntilIdle()
        assertEquals(YoutubeUiState(), empty.uiState.value)
        assertTrue(failed.uiState.value.hasLoadError)
        assertFalse(failed.uiState.value.isLoading)
        assertTrue(failed.uiState.value.channels.isEmpty())
    }
}
