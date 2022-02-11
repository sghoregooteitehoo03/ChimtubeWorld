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

@Composable
fun AddBookmarkRoute(
    viewModel: BookmarkViewModel = hiltViewModel(),
    gViewModel: GlobalViewModel,
    navController: NavController
) {
    val uiState by viewModel.state.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(key1 = uiState.errorMsg) {
        val msg = uiState.errorMsg
        if (msg.isNotEmpty()) {
            Toast.makeText(context, msg, Toast.LENGTH_SHORT)
                .show()

            if (uiState.completeBookmark == null) {
                viewModel.clearMsg()
            }
        }

        uiState.completeBookmark?.let {
            gViewModel.bookmarkData.value = it
            navController.navigateUp()
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