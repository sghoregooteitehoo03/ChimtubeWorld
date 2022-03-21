package com.sghore.chimtubeworld.presentation.bookmarkScreen

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.sghore.chimtubeworld.presentation.ui.GlobalViewModel
import kotlinx.coroutines.flow.collectLatest

@Composable
fun AddBookmarkRoute(
    viewModel: BookmarkViewModel = hiltViewModel(),
    gViewModel: GlobalViewModel,
    navController: NavController
) {
    val uiState by viewModel.state.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(key1 = true) {
        viewModel.event.collectLatest { event ->
            when (event) {
                is BookmarkViewModel.BookmarkEvent.ShowToastMessage -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT)
                        .show()
                }
                is BookmarkViewModel.BookmarkEvent.ChangeBookmark -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT)
                        .show()
                    gViewModel.bookmarkData = event.bookmark

                    navController.navigateUp()
                }
            }
        }
    }

    BookmarkScreen(
        uiState = uiState,
        buttonText = "만들기",
        onTitleChange = viewModel::setTitle,
        onVideoPositionChange = viewModel::setVideoPosition,
        onChangeBookmarkColor = viewModel::changeBookmarkColor,
        onButtonClick = {
            viewModel.addOrEditBookmark()
        }
    )
}