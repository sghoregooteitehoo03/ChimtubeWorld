package com.sghore.chimtubeworld.feature.youtube.impl.playlist_screen

import androidx.lifecycle.ViewModelStore
import com.sghore.chimtubeworld.core.testing.model.playlist
import com.sghore.chimtubeworld.core.testing.repository.FakeYoutubeRepository
import com.sghore.chimtubeworld.core.testing.rule.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PlaylistViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val store = ViewModelStore()

    @After
    fun tearDown() {
        store.clear()
    }

    @Test
    fun `메인 목록은 생성자의 채널과 분리한 전체 재생목록 ID를 요청한다`() = runTest(mainDispatcherRule.testDispatcher) {
        val repository = FakeYoutubeRepository()
        val vm = PlaylistViewModel(repository, "channel", "first|second", 0, "채널 제목").also { store.put("main", it) }
        assertEquals(listOf(FakeYoutubeRepository.MainPlaylistRequest("channel", listOf("first", "second"))), repository.mainPlaylistRequests)
        assertTrue(repository.subPlaylistRequests.isEmpty())
        assertEquals("채널 제목", vm.uiState.value.title)
        assertNotNull(vm.uiState.value.playlists)
    }

    @Test
    fun `서브 목록은 채널 없이 생성자의 재생목록 ID와 타입을 사용한다`() = runTest(mainDispatcherRule.testDispatcher) {
        val repository = FakeYoutubeRepository()
        val vm = PlaylistViewModel(repository, null, "one|two", 1, "외부 방송").also { store.put("sub", it) }
        assertEquals(listOf(listOf("one", "two")), repository.subPlaylistRequests)
        assertTrue(repository.mainPlaylistRequests.isEmpty())
        assertEquals("외부 방송", vm.uiState.value.title)
        val effects = mutableListOf<PlaylistUiSideEffect>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { vm.sideEffect.collect { effects += it } }
        val selected = playlist(id = "chosen", title = "선택한 목록")
        vm.onEvent(PlaylistUiEvent.PlaylistClicked(selected))
        vm.onEvent(PlaylistUiEvent.BackClicked)
        runCurrent()
        assertEquals(listOf(PlaylistUiSideEffect.NavigateToVideos(selected), PlaylistUiSideEffect.NavigateBack), effects)
        assertEquals(1, repository.subPlaylistRequests.size)
    }
}
