package com.sghore.chimtubeworld.feature.bookmark.impl.bookmark_screen

import androidx.lifecycle.ViewModelStore
import com.sghore.chimtubeworld.core.model.VideoSource
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
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class BookmarkViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val store = ViewModelStore()

    @After
    fun tearDown() {
        store.clear()
    }

    private lateinit var previousTimeZone: TimeZone

    @Before
    fun setTimeZone() {
        previousTimeZone = TimeZone.getDefault()
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"))
    }

    @After
    fun restoreTimeZone() { TimeZone.setDefault(previousTimeZone) }

    @Test
    fun `추가 진입은 공유 URL로 조회하고 입력 전에는 저장할 수 없다`() = runTest(mainDispatcherRule.testDispatcher) {
        val expected = video(duration = -28800000)
        val youtube = FakeYoutubeRepository(expected).apply { videoResponseDelayMillis = 1000 }
        val repository = FakeBookmarkRepository()
        val vm = BookmarkViewModel(youtube, repository, null, -1, null, "shared-url").also { store.put("bookmark", it) }
        assertTrue(vm.uiState.value.isLoading)
        runCurrent()
        assertEquals(listOf("shared-url"), youtube.videoRequests)
        advanceUntilIdle()
        assertSame(expected, vm.uiState.value.videoData)
        assertNull(vm.uiState.value.videoSource)
        assertFalse(vm.uiState.value.isEditing)
        assertFalse(vm.uiState.value.isLoading)
        assertFalse(vm.uiState.value.isEnable)
        vm.onEvent(BookmarkUiEvent.SaveClicked)
        runCurrent()
        assertTrue(repository.addRequests.isEmpty())
    }

    @Test
    fun `조회 실패는 로딩을 끝내고 오류 상태와 기존 토스트 의미를 전달한다`() = runTest(mainDispatcherRule.testDispatcher) {
        val youtube = FakeYoutubeRepository().apply { setVideoError(IllegalStateException("failure")) }
        val vm = BookmarkViewModel(youtube, FakeBookmarkRepository(), null, -1, null, "url").also { store.put("bookmark", it) }
        val effects = mutableListOf<BookmarkUiSideEffect>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { vm.sideEffect.collect { effects += it } }
        advanceUntilIdle()
        assertTrue(vm.uiState.value.hasLoadError)
        assertFalse(vm.uiState.value.isLoading)
        assertEquals(listOf(BookmarkUiSideEffect.LoadFailed), effects)
    }

    @Test
    fun `제목과 위치의 길이 제한 활성 조건 색상 입력을 유지한다`() = runTest(mainDispatcherRule.testDispatcher) {
        val vm = BookmarkViewModel(FakeYoutubeRepository(), FakeBookmarkRepository(), null, -1, null, "url").also { store.put("bookmark", it) }
        advanceUntilIdle()
        vm.onEvent(BookmarkUiEvent.TitleChanged("열글자까지12345".take(10)))
        val title = vm.uiState.value.bookmarkTitle
        vm.onEvent(BookmarkUiEvent.TitleChanged("12345678901"))
        assertEquals(title, vm.uiState.value.bookmarkTitle)
        assertFalse(vm.uiState.value.isEnable)
        vm.onEvent(BookmarkUiEvent.PositionChanged("01:00:00"))
        vm.onEvent(BookmarkUiEvent.PositionChanged("123456789"))
        assertEquals("01:00:00", vm.uiState.value.videoPosition)
        assertTrue(vm.uiState.value.isEnable)
        vm.onEvent(BookmarkUiEvent.ColorChanged(0x123456))
        assertEquals(0x123456, vm.uiState.value.selectedColor)
        vm.onEvent(BookmarkUiEvent.TitleChanged(""))
        assertFalse(vm.uiState.value.isEnable)
        vm.onEvent(BookmarkUiEvent.TitleChanged("제목"))
        vm.onEvent(BookmarkUiEvent.PositionChanged(""))
        assertFalse(vm.uiState.value.isEnable)
    }

    @Test
    fun `추가 저장은 입력 필드로 생성하고 조회한 ID와 결과 효과를 반환한다`() = runTest(mainDispatcherRule.testDispatcher) {
        val repository = FakeBookmarkRepository(initialItemId = 42)
        val vm = BookmarkViewModel(FakeYoutubeRepository(video(duration = -28800000)), repository, null, -1, null, "url").also { store.put("bookmark", it) }
        advanceUntilIdle()
        val effects = mutableListOf<BookmarkUiSideEffect>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { vm.sideEffect.collect { effects += it } }
        vm.onEvent(BookmarkUiEvent.TitleChanged("장면"))
        vm.onEvent(BookmarkUiEvent.PositionChanged("00:10"))
        vm.onEvent(BookmarkUiEvent.ColorChanged(123))
        vm.onEvent(BookmarkUiEvent.SaveClicked)
        advanceUntilIdle()
        val saved = repository.addRequests.single()
        assertNull(saved.id)
        assertEquals("video-1", saved.videoId)
        assertEquals("장면", saved.title)
        assertEquals(-32390000L, saved.videoPosition)
        assertEquals(123, saved.color)
        assertSame(saved, repository.itemIdRequests.single())
        val result = effects.single() as BookmarkUiSideEffect.BookmarkChanged
        assertEquals(BookmarkChange.ADDED, result.change)
        assertEquals(42, result.bookmark.id)
        assertEquals(saved.title, result.bookmark.title)
        assertEquals(saved.videoPosition, result.bookmark.videoPosition)
        assertEquals(saved.color, result.bookmark.color)
    }

    @Test
    fun `수정 진입은 선택 위치를 미리 채우고 기존 ID로 저장하며 ID를 재조회하지 않는다`() = runTest(mainDispatcherRule.testDispatcher) {
        val original = bookmark(id = 73, title = "기존 장면", videoPosition = -28800000, color = 321)
        val youtube = FakeYoutubeRepository()
        val repository = FakeBookmarkRepository()
        val vm = BookmarkViewModel(youtube, repository, video(duration = -25200000, bookmarks = listOf(bookmark(id = 1), original)), 1, VideoSource.YOUTUBE, null).also { store.put("bookmark", it) }
        assertTrue(vm.uiState.value.isEditing)
        assertFalse(vm.uiState.value.isLoading)
        assertTrue(vm.uiState.value.isEnable)
        assertEquals(original.title, vm.uiState.value.bookmarkTitle)
        assertEquals("01:00:00", vm.uiState.value.videoPosition)
        assertEquals(321, vm.uiState.value.selectedColor)
        assertEquals(VideoSource.YOUTUBE, vm.uiState.value.videoSource)
        assertTrue(youtube.videoRequests.isEmpty())
        val effects = mutableListOf<BookmarkUiSideEffect>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { vm.sideEffect.collect { effects += it } }
        vm.onEvent(BookmarkUiEvent.TitleChanged("바꾼 장면"))
        vm.onEvent(BookmarkUiEvent.PositionChanged("00:30"))
        vm.onEvent(BookmarkUiEvent.SaveClicked)
        advanceUntilIdle()
        val saved = repository.addRequests.single()
        assertEquals(73, saved.id)
        assertEquals("바꾼 장면", saved.title)
        assertEquals(-32370000L, saved.videoPosition)
        assertTrue(repository.itemIdRequests.isEmpty())
        val result = effects.single() as BookmarkUiSideEffect.BookmarkChanged
        assertEquals(BookmarkChange.EDITED, result.change)
        assertSame(saved, result.bookmark)
    }

    @Test
    fun `삭제 요청 취소 확인은 상태를 바꾸고 확인 후에만 저장소 삭제와 결과 표식을 전달한다`() = runTest(mainDispatcherRule.testDispatcher) {
        val original = bookmark(id = 9, videoPosition = -32390000)
        val repository = FakeBookmarkRepository()
        val vm = BookmarkViewModel(FakeYoutubeRepository(), repository, video(bookmarks = listOf(original)), 0, VideoSource.YOUTUBE, null).also { store.put("bookmark", it) }
        val effects = mutableListOf<BookmarkUiSideEffect>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { vm.sideEffect.collect { effects += it } }
        vm.onEvent(BookmarkUiEvent.DeleteConfirmed)
        vm.onEvent(BookmarkUiEvent.DeleteClicked)
        assertTrue(vm.uiState.value.isOpenDialog)
        vm.onEvent(BookmarkUiEvent.DialogDismissed)
        runCurrent()
        assertFalse(vm.uiState.value.isOpenDialog)
        assertTrue(repository.deleteRequests.isEmpty())
        assertTrue(effects.isEmpty())
        vm.onEvent(BookmarkUiEvent.DeleteClicked)
        vm.onEvent(BookmarkUiEvent.DeleteConfirmed)
        advanceUntilIdle()
        assertFalse(vm.uiState.value.isOpenDialog)
        assertSame(original, repository.deleteRequests.single())
        val result = effects.single() as BookmarkUiSideEffect.BookmarkChanged
        assertEquals(BookmarkChange.DELETED, result.change)
        assertEquals(original.id, result.bookmark.id)
        assertEquals(original.videoId, result.bookmark.videoId)
        assertEquals("", result.bookmark.title)
        assertEquals(0L, result.bookmark.videoPosition)
        assertEquals(original.color, result.bookmark.color)
    }

    @Test
    fun `복사 URL은 변경 중인 입력 대신 저장된 위치를 사용하고 소스별 URL 형식을 적용한다`() = runTest(mainDispatcherRule.testDispatcher) {
        val original = bookmark(videoPosition = -32390000)
        for (source in VideoSource.entries) {
            val vm = BookmarkViewModel(FakeYoutubeRepository(), FakeBookmarkRepository(), video(url = "original-url", bookmarks = listOf(original)), 0, source, null)
                .also { store.put(source.name, it) }
            val effects = mutableListOf<BookmarkUiSideEffect>()
            val job = backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { vm.sideEffect.collect { effects += it } }
            vm.onEvent(BookmarkUiEvent.PositionChanged("00:30"))
            vm.onEvent(BookmarkUiEvent.CopyUrlClicked)
            vm.onEvent(BookmarkUiEvent.BackClicked)
            runCurrent()
            val url = if (source == VideoSource.YOUTUBE) "original-url&t=10s" else "original-url?t=10s"
            assertEquals(listOf(BookmarkUiSideEffect.CopyUrl(url), BookmarkUiSideEffect.NavigateBack), effects)
            job.cancel()
        }
    }
}
