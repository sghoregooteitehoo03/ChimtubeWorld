package com.sghore.chimtubeworld.feature.youtube.impl.videos_screen

import androidx.lifecycle.ViewModelStore
import androidx.paging.LoadState
import androidx.paging.LoadStates
import androidx.paging.PagingData
import androidx.paging.testing.asSnapshot
import com.sghore.chimtubeworld.core.common.Constants
import com.sghore.chimtubeworld.core.model.VideoSource
import com.sghore.chimtubeworld.core.testing.model.bookmark
import com.sghore.chimtubeworld.core.testing.model.video
import com.sghore.chimtubeworld.core.testing.repository.FakeYoutubeRepository
import com.sghore.chimtubeworld.core.testing.rule.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class VideosViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val store = ViewModelStore()

    @After
    fun tearDown() {
        store.clear()
    }

    @Test
    fun `생성자의 첫 목록 ID로 paging을 요청하고 결과의 추가 수정 삭제를 해당 영상에 반영한다`() = runTest(mainDispatcherRule.testDispatcher) {
        val original = bookmark(id = 7, videoPosition = -32390000)
        val target = video(id = original.videoId, bookmarks = listOf(original))
        val other = video(id = "other")
        val repository = FakeYoutubeRepository().apply {
            // cachedIn keeps the outer Flow open; static data needs load states for asSnapshot.
            setVideosResult(flowOf(PagingData.from(
                data = listOf(target, other),
                sourceLoadStates = LoadStates(
                    refresh = LoadState.NotLoading(endOfPaginationReached = false),
                    prepend = LoadState.NotLoading(endOfPaginationReached = true),
                    append = LoadState.NotLoading(endOfPaginationReached = true)
                )
            )))
        }
        val vm = VideosViewModel(repository, "requested|ignored", "목록 제목", VideoSource.YOUTUBE).also { store.put("videos", it) }
        assertEquals(listOf("requested"), repository.videosRequests)
        assertEquals("목록 제목", vm.uiState.value.title)
        assertEquals(2, vm.uiState.value.videos!!.asSnapshot().size)

        val added = bookmark(id = 8, videoPosition = -32400000, title = "처음")
        vm.onEvent(VideosUiEvent.BookmarkResultReceived(added))
        runCurrent()
        var snapshot = vm.uiState.value.videos!!.asSnapshot()
        assertEquals(listOf(8, 7), snapshot[0].bookmarks.map { it.id })
        assertTrue(snapshot[1].bookmarks.isEmpty())

        val edited = original.copy(title = "수정한 장면", videoPosition = -32380000, color = 123)
        vm.onEvent(VideosUiEvent.BookmarkResultReceived(edited))
        runCurrent()
        snapshot = vm.uiState.value.videos!!.asSnapshot()
        val actual = snapshot[0].bookmarks.last()
        // Bookmark equality compares only IDs; verify the changed fields separately.
        assertEquals(edited.title, actual.title)
        assertEquals(edited.videoPosition, actual.videoPosition)
        assertEquals(edited.color, actual.color)

        vm.onEvent(VideosUiEvent.BookmarkResultReceived(edited.copy(title = "", videoPosition = 0)))
        runCurrent()
        snapshot = vm.uiState.value.videos!!.asSnapshot()
        assertEquals(listOf(8), snapshot[0].bookmarks.map { it.id })
        assertEquals("other", snapshot[1].id)
        assertEquals(1, repository.videosRequests.size)
    }

    @Test
    fun `북마크 없는 영상은 앱 실행 효과이고 있는 영상은 상태로 선택 창을 연다`() = runTest(mainDispatcherRule.testDispatcher) {
        val vm = VideosViewModel(FakeYoutubeRepository(), "list", "title", VideoSource.YOUTUBE).also { store.put("videos", it) }
        val effects = mutableListOf<VideosUiSideEffect>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { vm.sideEffect.collect { effects += it } }
        val plain = video()
        vm.onEvent(VideosUiEvent.VideoClicked(plain))
        runCurrent()
        assertNull(vm.uiState.value.selectedVideo)
        assertEquals(listOf(VideosUiSideEffect.OpenVideo(Constants.YOUTUBE_PACKAGE_NAME, plain.url, VideoSource.YOUTUBE)), effects)
        effects.clear()
        val selected = video(bookmarks = listOf(bookmark(videoPosition = -32390000)))
        vm.onEvent(VideosUiEvent.VideoClicked(selected))
        runCurrent()
        assertSame(selected, vm.uiState.value.selectedVideo)
        assertTrue(effects.isEmpty())
        vm.onEvent(VideosUiEvent.PositionSelected(-32390000))
        runCurrent()
        assertNull(vm.uiState.value.selectedVideo)
        assertEquals(listOf(VideosUiSideEffect.OpenVideo(Constants.YOUTUBE_PACKAGE_NAME, "${selected.url}&t=10s", VideoSource.YOUTUBE)), effects)
        vm.onEvent(VideosUiEvent.VideoClicked(selected))
        vm.onEvent(VideosUiEvent.DialogDismissed)
        assertNull(vm.uiState.value.selectedVideo)
    }

    @Test
    fun `트위치 실행과 처음부터 선택은 기존 패키지와 쿼리 없는 URL을 사용한다`() = runTest(mainDispatcherRule.testDispatcher) {
        val vm = VideosViewModel(FakeYoutubeRepository(), "list", "title", VideoSource.TWITCH).also { store.put("videos", it) }
        val effects = mutableListOf<VideosUiSideEffect>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { vm.sideEffect.collect { effects += it } }
        val selected = video(id = "vod", url = "https://twitch.tv/videos/vod", bookmarks = listOf(bookmark()))
        vm.onEvent(VideosUiEvent.VideoClicked(selected.copy(bookmarks = emptyList())))
        runCurrent()
        assertEquals(VideosUiSideEffect.OpenVideo(Constants.TWITCH_VIDEO_PACKAGE_NAME + "vod", selected.url, VideoSource.TWITCH), effects.single())
        effects.clear()
        vm.onEvent(VideosUiEvent.VideoClicked(selected))
        vm.onEvent(VideosUiEvent.PositionSelected(-32400000))
        runCurrent()
        assertEquals(VideosUiSideEffect.OpenVideo(Constants.TWITCH_VIDEO_PACKAGE_NAME + "vod", "${selected.url}?t=0s", VideoSource.TWITCH), effects.single())
    }

    @Test
    fun `편집 도움말 뒤로가기는 해당 효과만 전달한다`() = runTest(mainDispatcherRule.testDispatcher) {
        val vm = VideosViewModel(FakeYoutubeRepository(), "list", "title", VideoSource.YOUTUBE).also { store.put("videos", it) }
        val effects = mutableListOf<VideosUiSideEffect>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { vm.sideEffect.collect { effects += it } }
        val selected = video(bookmarks = listOf(bookmark()))
        vm.onEvent(VideosUiEvent.BookmarkClicked(selected, 0))
        vm.onEvent(VideosUiEvent.HelpClicked)
        vm.onEvent(VideosUiEvent.BackClicked)
        runCurrent()
        assertEquals(listOf(VideosUiSideEffect.NavigateToEditBookmark(selected, 0, VideoSource.YOUTUBE), VideosUiSideEffect.ShowHelp, VideosUiSideEffect.NavigateBack), effects)
    }
}
