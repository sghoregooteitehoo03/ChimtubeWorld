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
import com.sghore.chimtubeworld.other.Contents
import com.sghore.chimtubeworld.presentation.ui.GlobalViewModel

@Composable
fun EditBookmarkRoute(
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
            gViewModel.bookmarkData = it
            navController.navigateUp()
        }
    }

    LaunchedEffect(key1 = gViewModel.topAppBarAction) {
        val action = gViewModel.topAppBarAction // navigation menu 액션
        if (action == Contents.ACTION_COPY_URL) { // URL 복사
            clipData(
                viewModel = viewModel,
                context = context
            )
        } else if (action == Contents.ACTION_DELETE_BOOKMARK ) { // 북마크 삭제
            viewModel.setDialogState(true)
        }

        gViewModel.topAppBarAction = ""
    }

    BookmarkScreen(
        uiState = uiState,
        buttonText = "수정하기",
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
            text = { Text(text = "북마크를 삭제하시겠습니까?") },
            confirmButton = {
                Text(
                    text = "확인",
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
                    text = "취소",
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

// url 클립보드에 저장
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
    Toast.makeText(context, "URL이 복사되었습니다.", Toast.LENGTH_SHORT)
        .show()
}