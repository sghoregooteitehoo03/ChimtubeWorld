package com.sghore.chimtubeworld.presentation.bookmarkScreen

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.material.AlertDialog
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.sghore.chimtubeworld.presentation.ui.GlobalViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flattenMerge
import kotlinx.coroutines.flow.flowOf

@OptIn(FlowPreview::class)
@Composable
fun EditBookmarkRoute(
    viewModel: BookmarkViewModel = hiltViewModel(),
    gViewModel: GlobalViewModel,
    navController: NavController
) {
    val uiState by viewModel.state.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(key1 = true) {
        flowOf(viewModel.event, gViewModel.eventFlow).flattenMerge()
            .collectLatest { event ->
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
                    is GlobalViewModel.ActionEvent.CopyVideoUrl -> {
                        clipData(
                            viewModel = viewModel,
                            context = context
                        )
                    }
                    is GlobalViewModel.ActionEvent.DeleteBookmark -> {
                        viewModel.setDialogState(true)
                    }
                }
            }
    }

    BookmarkScreen(
        uiState = uiState,
        buttonText = "????????????",
        onTitleChange = viewModel::setTitle,
        onVideoPositionChange = viewModel::setVideoPosition,
        onChangeBookmarkColor = viewModel::changeBookmarkColor,
        onButtonClick = {
            viewModel.addOrEditBookmark(viewModel.selectedBookmark?.id!!)
        }
    )

    if (uiState.isOpenDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.setDialogState(false) },
            text = { Text(text = "???????????? ?????????????????????????") },
            confirmButton = {
                Text(
                    text = "??????",
                    modifier = Modifier
                        .padding(end = 16.dp, bottom = 16.dp)
                        .clickable {
                            viewModel.deleteBookmark(viewModel.selectedBookmark!!)
                            viewModel.setDialogState(false)
                        }
                )
            },
            dismissButton = {
                Text(
                    text = "??????",
                    modifier = Modifier
                        .padding(end = 16.dp, bottom = 16.dp)
                        .clickable {
                            viewModel.setDialogState(false)
                        }
                )
            }
        )
    }
}

// url ??????????????? ??????
private fun clipData(
    viewModel: BookmarkViewModel,
    context: Context
) {
    val bookmark = viewModel.selectedBookmark!!
    val url = viewModel.getVideoUrl(bookmark.videoPosition)
    val clipBoard =
        context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText("url", url)

    clipBoard.setPrimaryClip(clip)
    Toast.makeText(context, "URL??? ?????????????????????.", Toast.LENGTH_SHORT)
        .show()
}