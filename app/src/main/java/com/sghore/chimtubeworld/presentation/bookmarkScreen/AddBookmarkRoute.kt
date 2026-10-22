package com.sghore.chimtubeworld.presentation.bookmarkScreen

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import com.sghore.chimtubeworld.core.model.Bookmark
import kotlinx.coroutines.flow.collectLatest

@Composable
fun AddBookmarkRoute(
    viewModel: BookmarkViewModel,
    onBack: () -> Unit,
    onBookmarkChanged: (Bookmark) -> Unit
) {
    val uiState by viewModel.state.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(viewModel) {
        viewModel.event.collectLatest { event ->
            when (event) {
                is BookmarkViewModel.BookmarkEvent.ShowToastMessage -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT)
                        .show()
                }
                is BookmarkViewModel.BookmarkEvent.ChangeBookmark -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT)
                        .show()
                    onBookmarkChanged(event.bookmark)
                }
            }
        }
    }

    BookmarkScreen(
        uiState = uiState,
        buttonText = "만들기",
        onBack = onBack,
        onTitleChange = viewModel::setTitle,
        onVideoPositionChange = viewModel::setVideoPosition,
        onChangeBookmarkColor = viewModel::changeBookmarkColor,
        onButtonClick = {
            viewModel.addOrEditBookmark()
        }
    )
}
