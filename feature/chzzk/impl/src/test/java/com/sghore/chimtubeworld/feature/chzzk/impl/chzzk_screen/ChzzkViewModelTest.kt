package com.sghore.chimtubeworld.feature.chzzk.impl.chzzk_screen

import androidx.lifecycle.ViewModelStore
import com.sghore.chimtubeworld.core.testing.model.channel
import com.sghore.chimtubeworld.core.testing.repository.FakeTwitchRepository
import com.sghore.chimtubeworld.core.testing.rule.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ChzzkViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val viewModelStore = ViewModelStore()

    @After
    fun tearDown() {
        viewModelStore.clear()
    }

    @Test
    fun `최초 조회는 로딩 후 원래 목록과 첫 type 0 메인 채널을 표시한다`() =
        runTest(mainDispatcherRule.testDispatcher) {
            val crew = channel(id = "crew", type = 1)
            val firstMain = channel(id = "first-main", type = 0)
            val secondMain = channel(id = "second-main", type = 0)
            val channels = listOf(crew, firstMain, secondMain)
            val repository = FakeTwitchRepository(channels).apply {
                channelsResponseDelayMillis = 1_000L
            }
            val viewModel = createViewModel(repository)
            val effects = mutableListOf<ChzzkUiSideEffect>()
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                viewModel.sideEffect.collect { effects += it }
            }

            runCurrent()

            assertTrue(viewModel.uiState.value.isLoading)
            assertFalse(viewModel.uiState.value.hasLoadError)
            assertNull(viewModel.uiState.value.mainChannelInfo)
            assertTrue(viewModel.uiState.value.channels.isEmpty())

            advanceUntilIdle()

            val state = viewModel.uiState.value
            assertFalse(state.isLoading)
            assertFalse(state.hasLoadError)
            assertFalse(state.isDialogOpen)
            assertSame(firstMain, state.mainChannelInfo)
            assertEquals(channels, state.channels)
            assertEquals(1, repository.channelsCallCount)
            assertTrue(effects.isEmpty())
        }

    @Test
    fun `조회 실패는 로딩을 끝내고 데이터 없이 오류 상태를 표시한다`() =
        runTest(mainDispatcherRule.testDispatcher) {
            val repository = FakeTwitchRepository().apply {
                channelsResponseDelayMillis = 1_000L
                setChannelsError(IllegalStateException("조회 실패"))
            }
            val viewModel = createViewModel(repository)

            runCurrent()
            assertTrue(viewModel.uiState.value.isLoading)
            assertFalse(viewModel.uiState.value.hasLoadError)

            advanceUntilIdle()

            assertFalse(viewModel.uiState.value.isLoading)
            assertTrue(viewModel.uiState.value.hasLoadError)
            assertNull(viewModel.uiState.value.mainChannelInfo)
            assertTrue(viewModel.uiState.value.channels.isEmpty())
            assertFalse(viewModel.uiState.value.isDialogOpen)
            assertEquals(1, repository.channelsCallCount)
        }

    @Test
    fun `메인 채널 클릭과 닫기는 다이얼로그 상태만 변경한다`() =
        runTest(mainDispatcherRule.testDispatcher) {
            val viewModel = createViewModel(FakeTwitchRepository(listOf(channel())))
            advanceUntilIdle()
            val before = viewModel.uiState.value
            val effects = mutableListOf<ChzzkUiSideEffect>()
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                viewModel.sideEffect.collect { effects += it }
            }

            viewModel.onEvent(ChzzkUiEvent.MainChannelClicked)
            runCurrent()
            assertEquals(before.copy(isDialogOpen = true), viewModel.uiState.value)

            viewModel.onEvent(ChzzkUiEvent.DialogDismissed)
            runCurrent()

            assertEquals(before, viewModel.uiState.value)
            assertTrue(effects.isEmpty())
        }

    @Test
    fun `유튜브 선택은 다이얼로그를 닫고 기존 라이브 채널 URL을 전달한다`() =
        runTest(mainDispatcherRule.testDispatcher) {
            val main = channel(url = "https://chzzk.naver.com/live/main")
            val viewModel = createViewModel(FakeTwitchRepository(listOf(main)))
            advanceUntilIdle()
            val before = viewModel.uiState.value
            val effects = mutableListOf<ChzzkUiSideEffect>()
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                viewModel.sideEffect.collect { effects += it }
            }
            viewModel.onEvent(ChzzkUiEvent.MainChannelClicked)

            viewModel.onEvent(ChzzkUiEvent.YoutubeClicked)
            runCurrent()

            assertEquals(before, viewModel.uiState.value)
            assertEquals(
                listOf(ChzzkUiSideEffect.OpenYoutube("https://www.youtube.com/@ChimChakMan_Data")),
                effects
            )
        }

    @Test
    fun `치지직 선택은 다이얼로그를 닫고 메인 채널의 정확한 URL을 전달한다`() =
        runTest(mainDispatcherRule.testDispatcher) {
            val main = channel(url = "https://chzzk.naver.com/live/selected-main")
            val viewModel = createViewModel(FakeTwitchRepository(listOf(main)))
            advanceUntilIdle()
            val before = viewModel.uiState.value
            val effects = mutableListOf<ChzzkUiSideEffect>()
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                viewModel.sideEffect.collect { effects += it }
            }
            viewModel.onEvent(ChzzkUiEvent.MainChannelClicked)

            viewModel.onEvent(ChzzkUiEvent.ChzzkClicked)
            runCurrent()

            assertEquals(before, viewModel.uiState.value)
            assertEquals(listOf(ChzzkUiSideEffect.OpenUrl("https://chzzk.naver.com/live/selected-main")), effects)
        }

    @Test
    fun `각 크루 채널 클릭은 해당 URL만 한 번씩 전달하고 상태를 유지한다`() =
        runTest(mainDispatcherRule.testDispatcher) {
            val channels = listOf(
                channel(id = "main", url = "https://chzzk.naver.com/live/main"),
                channel(id = "crew", type = 1, url = "https://chzzk.naver.com/live/crew")
            )
            val viewModel = createViewModel(FakeTwitchRepository(channels))
            advanceUntilIdle()
            val before = viewModel.uiState.value
            val effects = mutableListOf<ChzzkUiSideEffect>()
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                viewModel.sideEffect.collect { effects += it }
            }

            channels.forEachIndexed { index, channel ->
                viewModel.onEvent(ChzzkUiEvent.CrewChannelClicked(channel))
                runCurrent()

                assertEquals(channels.take(index + 1).map { ChzzkUiSideEffect.OpenUrl(it.url) }, effects)
            }

            assertSame(before, viewModel.uiState.value)
        }

    @Test
    fun `type 0 채널이 없으면 오류 상태로 바꾸지 않고 기존처럼 예외를 전파한다`() {
        assertThrows(IndexOutOfBoundsException::class.java) {
            runTest(mainDispatcherRule.testDispatcher) {
                createViewModel(FakeTwitchRepository(listOf(channel(type = 1))))
                advanceUntilIdle()
            }
        }
    }

    private fun createViewModel(repository: FakeTwitchRepository): ChzzkViewModel =
        ChzzkViewModel(repository).also { viewModelStore.put("chzzk", it) }
}
