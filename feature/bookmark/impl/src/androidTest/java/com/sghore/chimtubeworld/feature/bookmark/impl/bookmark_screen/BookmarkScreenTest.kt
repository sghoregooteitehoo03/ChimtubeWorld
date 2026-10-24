package com.sghore.chimtubeworld.feature.bookmark.impl.bookmark_screen

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.test.espresso.Espresso.pressBack
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.sghore.chimtubeworld.core.designsystem.theme.ChimtubeWorldTheme
import com.sghore.chimtubeworld.feature.bookmark.impl.R
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BookmarkScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private fun setScreen(state: BookmarkUiState, onEvent: (BookmarkUiEvent) -> Unit = {}) {
        composeTestRule.setContent { ChimtubeWorldTheme { BookmarkScreen(state, onEvent) } }
    }

    @Test
    fun 빈_입력은_저장_비활성이며_추가_화면은_편집_액션이_없다() {
        setScreen(BookmarkUiState())
        composeTestRule.onNodeWithText(string(R.string.bookmark_create)).performScrollTo().assertIsNotEnabled()
        composeTestRule.onNodeWithContentDescription(string(R.string.bookmark_copy_url)).assertDoesNotExist()
        composeTestRule.onNodeWithContentDescription(string(R.string.bookmark_delete)).assertDoesNotExist()
    }

    @Test
    fun 제목과_위치_입력을_이벤트로_전달한다() {
        val events = mutableListOf<BookmarkUiEvent>()
        setScreen(BookmarkUiState()) { events += it }
        composeTestRule.onNode(hasSetTextAction() and hasText(string(R.string.bookmark_title_hint)))
            .performScrollTo().performTextInput("장면")
        composeTestRule.onNode(hasSetTextAction() and hasText(string(R.string.bookmark_position_hint)))
            .performScrollTo().performTextInput("00:10")
        composeTestRule.runOnIdle {
            assertEquals(listOf(BookmarkUiEvent.TitleChanged("장면"), BookmarkUiEvent.PositionChanged("00:10")), events)
        }
    }

    @Test
    fun 수정_상태는_기존_값과_활성_버튼을_표시하고_저장_복사_삭제_이벤트를_전달한다() {
        val events = mutableListOf<BookmarkUiEvent>()
        setScreen(BookmarkUiState(isEditing = true, bookmarkTitle = "기존 장면", videoPosition = "00:10", isEnable = true)) { events += it }
        composeTestRule.onNodeWithText("기존 장면").performScrollTo().assertIsDisplayed()
        composeTestRule.onNodeWithText("00:10").assertExists()
        composeTestRule.onNodeWithText(string(R.string.bookmark_edit)).performScrollTo().assertIsEnabled().performClick()
        composeTestRule.onNodeWithContentDescription(string(R.string.bookmark_copy_url)).performClick()
        composeTestRule.onNodeWithContentDescription(string(R.string.bookmark_delete)).performClick()
        composeTestRule.runOnIdle {
            assertEquals(listOf(BookmarkUiEvent.SaveClicked, BookmarkUiEvent.CopyUrlClicked, BookmarkUiEvent.DeleteClicked), events)
        }
        composeTestRule.onNode(isDialog()).assertDoesNotExist()
    }

    @Test
    fun 삭제_확인과_취소_버튼은_각_이벤트를_전달한다() {
        val events = mutableListOf<BookmarkUiEvent>()
        setScreen(BookmarkUiState(isEditing = true, isOpenDialog = true)) { events += it }
        composeTestRule.onNodeWithText(string(R.string.bookmark_delete_confirmation)).assertIsDisplayed()
        composeTestRule.onNodeWithText(string(R.string.bookmark_cancel)).performClick()
        composeTestRule.onNodeWithText(string(R.string.bookmark_confirm)).performClick()
        composeTestRule.runOnIdle {
            assertEquals(listOf(BookmarkUiEvent.DialogDismissed, BookmarkUiEvent.DeleteConfirmed), events)
        }
    }

    @Test
    fun 삭제_다이얼로그_뒤로가기는_닫기_이벤트다() {
        val events = mutableListOf<BookmarkUiEvent>()
        setScreen(BookmarkUiState(isEditing = true, isOpenDialog = true)) { events += it }
        composeTestRule.onNode(isDialog()).assertIsDisplayed()
        pressBack()
        composeTestRule.runOnIdle { assertEquals(listOf(BookmarkUiEvent.DialogDismissed), events) }
    }

    @Test
    fun 조회_실패는_리소스_오류_문구를_표시한다() {
        setScreen(BookmarkUiState(hasLoadError = true))
        composeTestRule.onNodeWithText(string(R.string.bookmark_load_error)).assertIsDisplayed()
    }

    private fun string(id: Int, vararg args: Any): String =
        InstrumentationRegistry.getInstrumentation().targetContext.getString(id, *args)
}
