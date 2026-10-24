package com.sghore.chimtubeworld.feature.bookmark.impl.navigation

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.sghore.chimtubeworld.core.navigation.Navigator
import com.sghore.chimtubeworld.feature.bookmark.api.navigation.AddBookmarkKey
import com.sghore.chimtubeworld.feature.bookmark.api.navigation.BookmarkResultKey
import com.sghore.chimtubeworld.feature.bookmark.api.navigation.EditBookmarkKey
import com.sghore.chimtubeworld.feature.bookmark.impl.R
import com.sghore.chimtubeworld.feature.bookmark.impl.bookmark_screen.BookmarkChange
import com.sghore.chimtubeworld.feature.bookmark.impl.bookmark_screen.BookmarkRoute
import com.sghore.chimtubeworld.feature.bookmark.impl.bookmark_screen.BookmarkUiSideEffect
import com.sghore.chimtubeworld.feature.bookmark.impl.bookmark_screen.BookmarkViewModel

fun EntryProviderScope<NavKey>.bookmarkEntry(navigator: Navigator) {
    entry<AddBookmarkKey> { key ->
        val context = LocalContext.current
        val viewModel = hiltViewModel<BookmarkViewModel, BookmarkViewModel.Factory>(
            creationCallback = { it.create(null, -1, null, key.url) }
        )
        BookmarkRoute(viewModel = viewModel, onSideEffect = { handleBookmarkEffect(it, context, navigator) })
    }

    entry<EditBookmarkKey> { key ->
        val context = LocalContext.current
        val viewModel = hiltViewModel<BookmarkViewModel, BookmarkViewModel.Factory>(
            creationCallback = { it.create(key.video, key.pos, key.source, null) }
        )
        BookmarkRoute(viewModel = viewModel, onSideEffect = { handleBookmarkEffect(it, context, navigator) })
    }
}

private fun handleBookmarkEffect(effect: BookmarkUiSideEffect, context: Context, navigator: Navigator) {
    when (effect) {
        BookmarkUiSideEffect.NavigateBack -> navigator.goBack()
        BookmarkUiSideEffect.InvalidPosition -> showToast(context, R.string.bookmark_invalid_position)
        BookmarkUiSideEffect.LoadFailed -> showToast(context, R.string.bookmark_load_error)
        is BookmarkUiSideEffect.CopyUrl -> {
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            clipboard.setPrimaryClip(ClipData.newPlainText("url", effect.url))
            showToast(context, R.string.bookmark_url_copied)
        }
        is BookmarkUiSideEffect.BookmarkChanged -> {
            val message = when (effect.change) {
                BookmarkChange.ADDED -> R.string.bookmark_added
                BookmarkChange.EDITED -> R.string.bookmark_edited
                BookmarkChange.DELETED -> R.string.bookmark_deleted
            }
            showToast(context, message)
            navigator.setResult(BookmarkResultKey, effect.bookmark)
            navigator.goBack()
        }
    }
}

private fun showToast(context: Context, message: Int) {
    Toast.makeText(context, context.getString(message), Toast.LENGTH_SHORT).show()
}
