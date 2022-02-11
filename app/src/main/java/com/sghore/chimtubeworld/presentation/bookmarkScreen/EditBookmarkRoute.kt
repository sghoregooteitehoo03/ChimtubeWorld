package com.sghore.chimtubeworld.presentation.bookmarkScreen

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.sghore.chimtubeworld.R
import com.sghore.chimtubeworld.data.model.Bookmark
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
            gViewModel.bookmarkData.value = it
            navController.navigateUp()
        }
    }

    LaunchedEffect(key1 = gViewModel.topAppBarAction) {
        val action = gViewModel.topAppBarAction
        if (action == Contents.ACTION_COPY_URL) {
            clipData(
                viewModel = viewModel,
                context = context
            )
        } else if (action == Contents.ACTION_DELETE_BOOKMARK) {
            deleteDialog(
                viewModel = viewModel,
                context = context
            )
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

// 북마크 삭제 다이얼로그
private fun deleteDialog(
    viewModel: BookmarkViewModel,
    context: Context
) {
    with(MaterialAlertDialogBuilder(context, R.style.AlertDialogTheme)) {
        setMessage("북마크를 삭제하시겠습니까?")
        setPositiveButton("확인") { dialog, which ->
            dialog.dismiss()
            viewModel.deleteBookmark(viewModel.selectedBookmark!!)
        }
        setNegativeButton("취소") { dialog, which ->
            dialog.cancel()
        }

        show()
    }
}