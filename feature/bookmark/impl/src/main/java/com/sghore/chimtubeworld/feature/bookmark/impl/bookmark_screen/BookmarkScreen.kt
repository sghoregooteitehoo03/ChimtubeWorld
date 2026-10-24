package com.sghore.chimtubeworld.feature.bookmark.impl.bookmark_screen

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sghore.chimtubeworld.core.designsystem.R as DesignSystemR
import com.sghore.chimtubeworld.core.designsystem.component.ScreenScaffold
import com.sghore.chimtubeworld.core.designsystem.theme.ChimtubeWorldTheme
import com.sghore.chimtubeworld.core.model.VideoSource
import com.sghore.chimtubeworld.feature.bookmark.impl.R
import com.sghore.chimtubeworld.feature.bookmark.impl.bookmark_screen.component.DeleteBookmarkDialog
import com.sghore.chimtubeworld.feature.bookmark.impl.bookmark_screen.component.InputBookmarkInfo
import com.sghore.chimtubeworld.feature.bookmark.impl.bookmark_screen.component.VideoInfo
import java.util.*

@Composable
internal fun BookmarkScreen(
    uiState: BookmarkUiState,
    onEvent: (BookmarkUiEvent) -> Unit
) {
    if (uiState.isOpenDialog) {
        DeleteBookmarkDialog(
            onConfirm = { onEvent(BookmarkUiEvent.DeleteConfirmed) },
            onDismiss = { onEvent(BookmarkUiEvent.DialogDismissed) }
        )
    }

    ScreenScaffold(title = stringResource(R.string.bookmark_title), onBack = { onEvent(BookmarkUiEvent.BackClicked) }, actions = {
        if (uiState.isEditing) {
            IconButton(onClick = { onEvent(BookmarkUiEvent.CopyUrlClicked) }) {
                Icon(Icons.AutoMirrored.Filled.Assignment, contentDescription = stringResource(R.string.bookmark_copy_url), tint = MaterialTheme.colorScheme.onPrimary)
            }
        }
        if (uiState.isEditing) {
            IconButton(onClick = { onEvent(BookmarkUiEvent.DeleteClicked) }) {
                Icon(Icons.Default.Delete, contentDescription = stringResource(R.string.bookmark_delete), tint = MaterialTheme.colorScheme.onPrimary)
            }
        }
    }) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .imePadding()
                .verticalScroll(
                    state = rememberScrollState()
                )
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    trackColor = Color.Transparent,
                    strokeCap = StrokeCap.Square,
                    gapSize = 0.dp,
                    modifier = Modifier.align(Alignment.Center),
                    color = colorResource(id = DesignSystemR.color.item_color)
                )
            } else {
                Column {
                    if (uiState.hasLoadError) {
                        Text(stringResource(R.string.bookmark_load_error), color = colorResource(DesignSystemR.color.item_color), modifier = Modifier.padding(12.dp))
                    }
                    VideoInfo(
                        video = uiState.videoData,
                        typeImage = if (uiState.videoSource == VideoSource.TWITCH) {
                            DesignSystemR.drawable.twitch
                        } else {
                            DesignSystemR.drawable.youtube
                        }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    InputBookmarkInfo(
                        uiState = uiState,
                        buttonText = stringResource(if (uiState.isEditing) R.string.bookmark_edit else R.string.bookmark_create),
                        onTitleChange = { onEvent(BookmarkUiEvent.TitleChanged(it)) },
                        onVideoPositionChange = { onEvent(BookmarkUiEvent.PositionChanged(it)) },
                        onChangeBookmarkColor = { onEvent(BookmarkUiEvent.ColorChanged(it)) },
                        onButtonClick = { onEvent(BookmarkUiEvent.SaveClicked) },
                        modifier = Modifier.padding(start = 12.dp, end = 12.dp)
                    )
                }
            }
        }
    }
}

@Preview(name = "Add Light", showBackground = true)
@Preview(name = "Add Dark", showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun AddBookmarkScreenPreview() {
    ChimtubeWorldTheme(darkTheme = isSystemInDarkTheme()) {
        BookmarkScreen(BookmarkUiState(), {})
    }
}

@Preview(name = "Edit Light", showBackground = true)
@Preview(name = "Edit Dark", showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun EditBookmarkScreenPreview() {
    ChimtubeWorldTheme(darkTheme = isSystemInDarkTheme()) {
        BookmarkScreen(BookmarkUiState(isEditing = true, bookmarkTitle = "북마크", videoPosition = "00:00", isEnable = true), {})
    }
}
