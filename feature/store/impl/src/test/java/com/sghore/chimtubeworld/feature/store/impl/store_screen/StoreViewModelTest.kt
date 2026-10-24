package com.sghore.chimtubeworld.feature.store.impl.store_screen

import androidx.lifecycle.ViewModelStore
import com.sghore.chimtubeworld.core.testing.rule.MainDispatcherRule
import com.sghore.chimtubeworld.feature.store.impl.R
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class StoreViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val viewModelStore = ViewModelStore()

    @After
    fun tearDown() {
        viewModelStore.clear()
    }

    @Test
    fun `초기 상태는 기존 순서의 두 상점을 표시하고 effect를 발생시키지 않는다`() =
        runTest(mainDispatcherRule.testDispatcher) {
            val viewModel = createViewModel()
            val effects = mutableListOf<StoreUiSideEffect>()
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                viewModel.sideEffect.collect { effects += it }
            }

            runCurrent()

            val stores = viewModel.uiState.value.storeInfoList
            assertEquals(listOf("침착맨", "얼렁뚱땅 상점"), stores.map { it.channelName })
            assertEquals(listOf(R.drawable.marple, R.drawable.naver), stores.map { it.channelImage })
            assertEquals(
                listOf("https://smartstore.naver.com/chim_chungja/best?cp=1", "https://uldd.net/collabochim"),
                stores.map { it.url }
            )
            assertTrue(effects.isEmpty())
        }

    @Test
    fun `각 상점을 클릭하면 해당 URL의 effect만 한 번씩 전달하고 상태를 유지한다`() =
        runTest(mainDispatcherRule.testDispatcher) {
            val viewModel = createViewModel()
            val initialState = viewModel.uiState.value
            val effects = mutableListOf<StoreUiSideEffect>()
            // replay 없는 stream이므로 event 전에 같은 scheduler에서 수집한다.
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                viewModel.sideEffect.collect { effects += it }
            }
            val expected = listOf(
                StoreUiSideEffect.OpenUrl("https://smartstore.naver.com/chim_chungja/best?cp=1"),
                StoreUiSideEffect.OpenUrl("https://uldd.net/collabochim")
            )

            initialState.storeInfoList.forEachIndexed { index, storeInfo ->
                viewModel.onEvent(StoreUiEvent.StoreClicked(storeInfo))
                runCurrent()

                assertEquals(expected.take(index + 1), effects)
            }

            advanceUntilIdle()

            assertEquals(expected, effects)
            assertSame(initialState, viewModel.uiState.value)
        }

    private fun createViewModel(): StoreViewModel =
        StoreViewModel().also { viewModelStore.put("store", it) }
}
