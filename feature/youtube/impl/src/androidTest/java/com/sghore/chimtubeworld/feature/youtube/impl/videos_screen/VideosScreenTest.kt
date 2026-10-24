package com.sghore.chimtubeworld.feature.youtube.impl.videos_screen

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.paging.PagingData
import androidx.test.espresso.Espresso.pressBack
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.sghore.chimtubeworld.core.common.parseDateTextFromPosition
import com.sghore.chimtubeworld.core.designsystem.theme.ChimtubeWorldTheme
import com.sghore.chimtubeworld.core.model.Bookmark
import com.sghore.chimtubeworld.core.model.Video
import com.sghore.chimtubeworld.feature.youtube.impl.R
import kotlinx.coroutines.flow.flowOf
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class VideosScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val bookmark = Bookmark(7, "video", "선택한 장면", -32390000, 0xFFFF0000.toInt())
    private val video = Video("video", "채널", "영상 제목", "", 0, 100, -31800000, "https://youtube.com/watch?v=video", listOf(bookmark))

    private fun setScreen(state: VideosUiState, onEvent: (VideosUiEvent) -> Unit) {
        composeTestRule.setContent { ChimtubeWorldTheme { VideosScreen(state, onEvent) } }
    }

    @Test
    fun 목록과_북마크_클릭은_영상과_선택_위치를_전달한다() {
        val events = mutableListOf<VideosUiEvent>()
        setScreen(VideosUiState(title = "목록", videos = flowOf(PagingData.from(listOf(video))))) { events += it }
        composeTestRule.onNodeWithText(video.title).performScrollTo().assertIsDisplayed().performClick()
        composeTestRule.onNodeWithText(bookmark.title).performScrollTo().assertIsDisplayed().performClick()
        composeTestRule.onNodeWithContentDescription(string(R.string.videos_help)).performClick()
        composeTestRule.runOnIdle {
            assertEquals(listOf(VideosUiEvent.VideoClicked(video), VideosUiEvent.BookmarkClicked(video, 0), VideosUiEvent.HelpClicked), events)
        }
        composeTestRule.onNode(isDialog()).assertDoesNotExist()
    }

    @Test
    fun 선택_다이얼로그는_처음부터와_북마크_위치를_전달한다() {
        val events = mutableListOf<VideosUiEvent>()
        setScreen(VideosUiState(selectedVideo = video)) { events += it }
        composeTestRule.onNode(isDialog()).assertIsDisplayed()
        composeTestRule.onNodeWithText(string(R.string.videos_select_position)).assertIsDisplayed()
        composeTestRule.onNodeWithText(string(R.string.videos_from_start)).performClick()
        composeTestRule.onNodeWithText(string(R.string.videos_bookmark_position, bookmark.title, parseDateTextFromPosition(bookmark.videoPosition))).performClick()
        composeTestRule.runOnIdle {
            assertEquals(listOf(VideosUiEvent.PositionSelected(-32400000), VideosUiEvent.PositionSelected(bookmark.videoPosition)), events)
        }
    }

    @Test
    fun 선택_다이얼로그_뒤로가기는_닫기_이벤트다() {
        val events = mutableListOf<VideosUiEvent>()
        setScreen(VideosUiState(selectedVideo = video)) { events += it }
        composeTestRule.onNode(isDialog()).assertIsDisplayed()
        pressBack()
        composeTestRule.runOnIdle { assertEquals(listOf(VideosUiEvent.DialogDismissed), events) }
    }

    private fun string(id: Int, vararg args: Any): String =
        InstrumentationRegistry.getInstrumentation().targetContext.getString(id, *args)
}
