package com.sghore.chimtubeworld.feature.bookmark.impl.bookmark_screen.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sghore.chimtubeworld.core.designsystem.theme.ChimtubeWorldTheme
import com.sghore.chimtubeworld.feature.bookmark.impl.R

@Composable
internal fun DeleteBookmarkDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 0.dp,
        shape = MaterialTheme.shapes.medium,
        textContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
        onDismissRequest = onDismiss,
        text = { Text(stringResource(R.string.bookmark_delete_confirmation), style = MaterialTheme.typography.bodyMedium) },
        confirmButton = {
            Text(
                stringResource(R.string.bookmark_confirm),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.87f),
                modifier = Modifier.clickable(onClick = onConfirm)
            )
        },
        dismissButton = {
            Text(
                stringResource(R.string.bookmark_cancel),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.87f),
                modifier = Modifier.padding(end = 8.dp).clickable(onClick = onDismiss)
            )
        }
    )
}

@Preview(showBackground = true)
@Composable
private fun DeleteBookmarkDialogPreview() {
    ChimtubeWorldTheme { DeleteBookmarkDialog({}, {}) }
}
