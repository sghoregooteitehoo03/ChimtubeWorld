package com.sghore.chimtubeworld.feature.webtoon.impl.webtoon_screen

import androidx.lifecycle.ViewModelStore
import com.sghore.chimtubeworld.core.testing.rule.MainDispatcherRule
import com.sghore.chimtubeworld.feature.webtoon.impl.R
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
class WebtoonViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val viewModelStore = ViewModelStore()

    @After
    fun tearDown() {
        viewModelStore.clear()
    }

    @Test
    fun `초기 상태는 기존 순서의 다섯 작품을 표시하고 effect를 발생시키지 않는다`() =
        runTest(mainDispatcherRule.testDispatcher) {
            val viewModel = createViewModel()
            val effects = mutableListOf<WebtoonUiSideEffect>()
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                viewModel.sideEffect.collect { effects += it }
            }

            runCurrent()

            val webtoons = viewModel.uiState.value.webtoonInfos
            assertEquals(listOf("103759", "704595", "602921", "557527", "687127"), webtoons.map { it.id })
            assertEquals(
                listOf("이말년씨리즈", "이말년씨리즈 2018", "이말년 서유기", "맨 vs 던전", "본격 말년런 만화"),
                webtoons.map { it.name }
            )
            assertEquals(
                listOf(listOf("2009~2012"), listOf("2018"), listOf("2013~2016"), listOf("2013"), listOf("2016")),
                webtoons.map { it.explains.toList() }
            )
            assertEquals(
                listOf(
                    R.drawable.webtoon_cover_1,
                    R.drawable.webtoon_cover_2,
                    R.drawable.webtoon_cover_3,
                    R.drawable.webtoon_cover_4,
                    R.drawable.webtoon_cover_5
                ).map { it.toString() },
                webtoons.map { it.image }
            )
            assertEquals(
                listOf(
                    "https://comic.naver.com/webtoon/list?titleId=103759",
                    "https://comic.naver.com/webtoon/list?titleId=704595",
                    "https://comic.naver.com/webtoon/list?titleId=602921",
                    "https://comic.naver.com/webtoon/list?titleId=557527",
                    "https://comic.naver.com/webtoon/list?titleId=687127"
                ),
                webtoons.map { it.url }
            )
            assertEquals(List(5) { 0 }, webtoons.map { it.type })
            assertTrue(effects.isEmpty())
        }

    @Test
    fun `각 작품을 클릭하면 해당 ID와 URL의 effect만 한 번씩 전달하고 상태를 유지한다`() =
        runTest(mainDispatcherRule.testDispatcher) {
            val viewModel = createViewModel()
            val initialState = viewModel.uiState.value
            val effects = mutableListOf<WebtoonUiSideEffect>()
            // replay 없는 stream이므로 event 전에 같은 scheduler에서 수집한다.
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                viewModel.sideEffect.collect { effects += it }
            }
            val expected = listOf(
                WebtoonUiSideEffect.OpenWebtoon("103759", "https://comic.naver.com/webtoon/list?titleId=103759"),
                WebtoonUiSideEffect.OpenWebtoon("704595", "https://comic.naver.com/webtoon/list?titleId=704595"),
                WebtoonUiSideEffect.OpenWebtoon("602921", "https://comic.naver.com/webtoon/list?titleId=602921"),
                WebtoonUiSideEffect.OpenWebtoon("557527", "https://comic.naver.com/webtoon/list?titleId=557527"),
                WebtoonUiSideEffect.OpenWebtoon("687127", "https://comic.naver.com/webtoon/list?titleId=687127")
            )

            initialState.webtoonInfos.forEachIndexed { index, webtoon ->
                viewModel.onEvent(WebtoonUiEvent.WebtoonClicked(webtoon))
                runCurrent()

                assertEquals(expected.take(index + 1), effects)
            }

            advanceUntilIdle()

            assertEquals(expected, effects)
            assertSame(initialState, viewModel.uiState.value)
        }

    private fun createViewModel(): WebtoonViewModel =
        WebtoonViewModel().also { viewModelStore.put("webtoon", it) }
}
