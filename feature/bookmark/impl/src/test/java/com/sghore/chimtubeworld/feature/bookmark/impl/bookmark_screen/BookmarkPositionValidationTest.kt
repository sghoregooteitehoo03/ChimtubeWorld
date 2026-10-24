package com.sghore.chimtubeworld.feature.bookmark.impl.bookmark_screen

import androidx.lifecycle.ViewModelStore
import com.sghore.chimtubeworld.core.testing.model.bookmark
import com.sghore.chimtubeworld.core.testing.model.video
import com.sghore.chimtubeworld.core.testing.repository.FakeBookmarkRepository
import com.sghore.chimtubeworld.core.testing.repository.FakeYoutubeRepository
import com.sghore.chimtubeworld.core.testing.rule.MainDispatcherRule
import java.util.TimeZone
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(Parameterized::class)
class BookmarkPositionValidationTest(private val input: String, private val expectedPosition: Long?) {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val store = ViewModelStore()
    private lateinit var previousTimeZone: TimeZone

    @Before
    fun setUp() {
        previousTimeZone = TimeZone.getDefault()
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"))
    }

    @After
    fun tearDown() {
        store.clear()
        TimeZone.setDefault(previousTimeZone)
    }

    @Test
    fun `위치 형식과 영상 범위에 따라 저장하거나 기존 오류 의미 효과를 전달한다`() = runTest(mainDispatcherRule.testDispatcher) {
        val repository = FakeBookmarkRepository()
        val vm = BookmarkViewModel(FakeYoutubeRepository(video(duration = -28800000)), repository, null, -1, null, "url")
            .also { store.put("bookmark", it) }
        advanceUntilIdle()
        val effects = mutableListOf<BookmarkUiSideEffect>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { vm.sideEffect.collect { effects += it } }
        vm.onEvent(BookmarkUiEvent.TitleChanged("장면"))
        vm.onEvent(BookmarkUiEvent.PositionChanged(input))
        vm.onEvent(BookmarkUiEvent.SaveClicked)
        advanceUntilIdle()
        if (expectedPosition == null) {
            assertTrue(repository.addRequests.isEmpty())
            assertEquals(listOf(BookmarkUiSideEffect.InvalidPosition), effects)
        } else {
            assertEquals(expectedPosition, repository.addRequests.single().videoPosition)
            assertEquals(BookmarkChange.ADDED, (effects.single() as BookmarkUiSideEffect.BookmarkChanged).change)
        }
    }

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{index}: position=<{0}>, parsed=<{1}>")
        fun cases(): List<Array<Any?>> = listOf(
            arrayOf("00:00", -32400000L),
            arrayOf("00:10", -32390000L),
            arrayOf("01:00:00", -28800000L),
            // SimpleDateFormat's existing lenient normalization is intentionally retained.
            arrayOf("00:60", -32340000L),
            arrayOf("60:00", -28800000L),
            arrayOf("61:00", null),
            arrayOf("01:00:01", null),
            arrayOf("123", null),
            arrayOf("ab:cd", null)
        )
    }
}
