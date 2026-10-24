package com.sghore.chimtubeworld.feature.youtube.impl.videos_screen.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.Dialog
import com.sghore.chimtubeworld.core.designsystem.theme.ChimtubeWorldTheme
import com.sghore.chimtubeworld.core.model.Bookmark

@Composable
internal fun SelectBookmarkDialog(
    bookmarkList: List<Bookmark>,
    onPositionSelected: (Long) -> Unit,
    onDismissRequest: () -> Unit
) {
    Dialog(onDismissRequest = onDismissRequest) {
        SelectBookmarkContent(bookmarkList, onPositionSelected)
    }
}

@Preview(showBackground = true)
@Composable
private fun SelectBookmarkDialogPreview() {
    ChimtubeWorldTheme { SelectBookmarkDialog(emptyList(), {}, {}) }
}
