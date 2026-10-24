package com.sghore.chimtubeworld.feature.bookmark.impl.navigation

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.sghore.chimtubeworld.core.navigation.Navigator
import com.sghore.chimtubeworld.feature.bookmark.api.navigation.AddBookmarkKey
import com.sghore.chimtubeworld.feature.bookmark.api.navigation.BookmarkResultKey
import com.sghore.chimtubeworld.feature.bookmark.api.navigation.EditBookmarkKey
import com.sghore.chimtubeworld.feature.bookmark.impl.bookmark_screen.AddBookmarkRoute
import com.sghore.chimtubeworld.feature.bookmark.impl.bookmark_screen.BookmarkViewModel
import com.sghore.chimtubeworld.feature.bookmark.impl.bookmark_screen.EditBookmarkRoute

fun EntryProviderScope<NavKey>.bookmarkEntry(navigator: Navigator) {
    entry<AddBookmarkKey> { key ->
        val viewModel = hiltViewModel<BookmarkViewModel, BookmarkViewModel.Factory>(
            creationCallback = { it.create(null, -1, null, key.url) }
        )
        AddBookmarkRoute(
            viewModel = viewModel,
            onBack = { navigator.goBack() },
            onBookmarkChanged = { bookmark ->
                navigator.setResult(BookmarkResultKey, bookmark)
                navigator.goBack()
            }
        )
    }

    entry<EditBookmarkKey> { key ->
        val viewModel = hiltViewModel<BookmarkViewModel, BookmarkViewModel.Factory>(
            creationCallback = { it.create(key.video, key.pos, key.source, null) }
        )
        EditBookmarkRoute(
            viewModel = viewModel,
            onBack = { navigator.goBack() },
            onBookmarkChanged = { bookmark ->
                navigator.setResult(BookmarkResultKey, bookmark)
                navigator.goBack()
            }
        )
    }
}
