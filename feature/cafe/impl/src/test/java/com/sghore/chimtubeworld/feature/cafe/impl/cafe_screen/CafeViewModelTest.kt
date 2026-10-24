package com.sghore.chimtubeworld.feature.cafe.impl.cafe_screen

import androidx.lifecycle.ViewModelStore
import androidx.paging.PagingData
import com.sghore.chimtubeworld.core.testing.model.channel
import com.sghore.chimtubeworld.core.testing.model.post
import com.sghore.chimtubeworld.core.testing.repository.FakeCafeRepository
import com.sghore.chimtubeworld.core.testing.rule.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotSame
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CafeViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val viewModelStore = ViewModelStore()

    @After
    fun tearDown() {
        viewModelStore.clear()
    }

    @Test
    fun `최초 조회는 로딩 후 카페 정보와 전체 카테고리 Paging을 표시한다`() =
        runTest(mainDispatcherRule.testDispatcher) {
            val cafe = channel(id = "cafe", url = "https://cafe.naver.com/zilioner")
            val repository = FakeCafeRepository(initialInfo = cafe).apply {
                infoResponseDelayMillis = 1_000L
            }
            val viewModel = createViewModel(repository)
            val effects = mutableListOf<CafeUiSideEffect>()
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                viewModel.sideEffect.collect { effects += it }
            }

            runCurrent()

            assertTrue(viewModel.uiState.value.isLoading)
            assertFalse(viewModel.uiState.value.hasLoadError)
            assertNull(viewModel.uiState.value.cafeInfo)
            assertTrue(repository.postsRequests.isEmpty())

            advanceUntilIdle()

            val state = viewModel.uiState.value
            assertFalse(state.isLoading)
            assertFalse(state.hasLoadError)
            assertSame(cafe, state.cafeInfo)
            assertEquals(0, state.cafeCategoryId)
            assertTrue(state.cafePosts != null)
            assertTrue(state.readHistory.isEmpty())
            assertEquals(1, repository.infoCallCount)
            assertEquals(listOf(0), repository.postsRequests)
            assertTrue(effects.isEmpty())
        }

    @Test
    fun `카페 조회 실패는 로딩을 끝내고 데이터 없이 오류 상태를 표시한다`() =
        runTest(mainDispatcherRule.testDispatcher) {
            val repository = FakeCafeRepository().apply {
                infoResponseDelayMillis = 1_000L
                setInfoError(IllegalStateException("조회 실패"))
            }
            val viewModel = createViewModel(repository)

            runCurrent()
            assertTrue(viewModel.uiState.value.isLoading)
            assertFalse(viewModel.uiState.value.hasLoadError)

            advanceUntilIdle()

            assertFalse(viewModel.uiState.value.isLoading)
            assertTrue(viewModel.uiState.value.hasLoadError)
            assertNull(viewModel.uiState.value.cafeInfo)
            assertNull(viewModel.uiState.value.cafePosts)
            assertEquals(1, repository.infoCallCount)
            assertTrue(repository.postsRequests.isEmpty())
        }

    @Test
    fun `카테고리 변경은 해당 ID를 요청하고 Paging을 교체하며 각 Flow를 캐시한다`() =
        runTest(mainDispatcherRule.testDispatcher) {
            var allCollections = 0
            var noticeCollections = 0
            val repository = FakeCafeRepository().apply {
                setPostsResult(flow {
                    allCollections++
                    emit(PagingData.from(listOf(post(id = 10))))
                })
            }
            val viewModel = createViewModel(repository)
            advanceUntilIdle()
            val before = viewModel.uiState.value
            val allPosts = checkNotNull(before.cafePosts)
            var allEmissions = 0
            repeat(2) {
                backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                    allPosts.take(1).collect { allEmissions++ }
                }
            }
            runCurrent()
            assertEquals(2, allEmissions)
            assertEquals(1, allCollections)

            repository.setPostsResult(flow {
                noticeCollections++
                emit(PagingData.from(listOf(post(id = 55))))
            })
            viewModel.onEvent(CafeUiEvent.CategoryClicked(5))
            val selected = viewModel.uiState.value
            val noticePosts = checkNotNull(selected.cafePosts)
            var noticeEmissions = 0
            repeat(2) {
                backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                    noticePosts.take(1).collect { noticeEmissions++ }
                }
            }
            runCurrent()

            assertEquals(listOf(0, 5), repository.postsRequests)
            assertEquals(5, selected.cafeCategoryId)
            assertNotSame(allPosts, noticePosts)
            assertSame(before.cafeInfo, selected.cafeInfo)
            assertEquals(2, noticeEmissions)
            assertEquals(1, noticeCollections)
            assertEquals(1, repository.infoCallCount)
        }

    @Test
    fun `게시글 클릭은 즉시 읽음 상태와 URL effect를 전달하고 해당 ID를 저장한다`() =
        runTest(mainDispatcherRule.testDispatcher) {
            val repository = FakeCafeRepository().apply { readResponseDelayMillis = 1_000L }
            val viewModel = createViewModel(repository)
            advanceUntilIdle()
            val clicked = post(id = 731, url = "https://cafe.naver.com/zilioner/731")
            val effects = mutableListOf<CafeUiSideEffect>()
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                viewModel.sideEffect.collect { effects += it }
            }

            viewModel.onEvent(CafeUiEvent.PostClicked(clicked))
            runCurrent()

            assertEquals(mapOf(731 to true), viewModel.uiState.value.readHistory)
            assertEquals(listOf(731), repository.readRequests)
            assertEquals(listOf(CafeUiSideEffect.OpenUrl("https://cafe.naver.com/zilioner/731")), effects)

            advanceUntilIdle()
            assertEquals(1, effects.size)

            viewModel.onEvent(CafeUiEvent.CategoryClicked(42))
            assertEquals(mapOf(731 to true), viewModel.uiState.value.readHistory)
        }

    @Test
    fun `읽음 저장 실패를 삼켜도 읽음 표시와 URL 실행은 유지한다`() =
        runTest(mainDispatcherRule.testDispatcher) {
            val repository = FakeCafeRepository().apply { setReadError(IllegalStateException("저장 실패")) }
            val viewModel = createViewModel(repository)
            advanceUntilIdle()
            val effects = mutableListOf<CafeUiSideEffect>()
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                viewModel.sideEffect.collect { effects += it }
            }
            val clicked = post(id = 99, url = "https://cafe.naver.com/zilioner/99")

            viewModel.onEvent(CafeUiEvent.PostClicked(clicked))
            runCurrent()

            assertEquals(listOf(99), repository.readRequests)
            assertEquals(mapOf(99 to true), viewModel.uiState.value.readHistory)
            assertFalse(viewModel.uiState.value.hasLoadError)
            assertEquals(listOf(CafeUiSideEffect.OpenUrl("https://cafe.naver.com/zilioner/99")), effects)
        }

    @Test
    fun `이미 읽은 게시글은 저장 없이 URL만 열며 null URL은 기존처럼 빈 값으로 전달한다`() =
        runTest(mainDispatcherRule.testDispatcher) {
            val repository = FakeCafeRepository()
            val viewModel = createViewModel(repository)
            advanceUntilIdle()
            val before = viewModel.uiState.value
            val effects = mutableListOf<CafeUiSideEffect>()
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                viewModel.sideEffect.collect { effects += it }
            }

            viewModel.onEvent(CafeUiEvent.PostClicked(post(isRead = true, url = null)))
            runCurrent()

            assertTrue(repository.readRequests.isEmpty())
            assertSame(before, viewModel.uiState.value)
            assertEquals(listOf(CafeUiSideEffect.OpenUrl("")), effects)
        }

    @Test
    fun `배너와 침하하 클릭은 정확한 URL을 한 번씩 전달하고 상태를 유지한다`() =
        runTest(mainDispatcherRule.testDispatcher) {
            val viewModel = createViewModel(FakeCafeRepository())
            advanceUntilIdle()
            val before = viewModel.uiState.value
            val effects = mutableListOf<CafeUiSideEffect>()
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                viewModel.sideEffect.collect { effects += it }
            }

            viewModel.onEvent(CafeUiEvent.BannerClicked("https://cafe.naver.com/zilioner"))
            runCurrent()
            assertEquals(listOf(CafeUiSideEffect.OpenUrl("https://cafe.naver.com/zilioner")), effects)

            viewModel.onEvent(CafeUiEvent.ChimhahaClicked)
            runCurrent()

            assertEquals(
                listOf(
                    CafeUiSideEffect.OpenUrl("https://cafe.naver.com/zilioner"),
                    CafeUiSideEffect.OpenUrl("https://chimhaha.net/")
                ),
                effects
            )
            assertSame(before, viewModel.uiState.value)
        }

    private fun createViewModel(repository: FakeCafeRepository): CafeViewModel =
        CafeViewModel(repository).also { viewModelStore.put("cafe", it) }
}
