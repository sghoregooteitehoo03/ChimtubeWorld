package com.sghore.chimtubeworld.presentation.ui

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.sghore.chimtubeworld.core.navigation.toEntries
import com.sghore.chimtubeworld.feature.bookmark.api.navigation.AddBookmarkKey
import com.sghore.chimtubeworld.feature.bookmark.api.navigation.EditBookmarkKey
import com.sghore.chimtubeworld.feature.bookmark.api.navigation.navigateToAddBookmark
import com.sghore.chimtubeworld.feature.bookmark.impl.navigation.bookmarkEntry
import com.sghore.chimtubeworld.feature.cafe.impl.navigation.cafeEntry
import com.sghore.chimtubeworld.feature.chzzk.impl.navigation.chzzkEntry
import com.sghore.chimtubeworld.feature.store.impl.navigation.storeEntry
import com.sghore.chimtubeworld.feature.webtoon.impl.navigation.webtoonEntry
import com.sghore.chimtubeworld.feature.youtube.impl.navigation.youtubeEntry
import com.sghore.chimtubeworld.presentation.BottomNavigationBar

@Composable
internal fun ChimtubeWorldApp(
    pendingShareUrls: List<String>,
    onShareHandled: () -> Unit,
    appState: AppState = rememberAppState()
) {
    val navigationState = appState.navigationState
    val navigator = appState.navigator
    val entries = remember(navigator) {
        entryProvider {
            youtubeEntry(navigator)
            bookmarkEntry(navigator)
            webtoonEntry()
            storeEntry()
            cafeEntry()
            chzzkEntry()
        }
    }
    val shareUrl = pendingShareUrls.firstOrNull()
    LaunchedEffect(shareUrl, pendingShareUrls.size, navigator) {
        if (shareUrl != null) {
            navigator.navigateToAddBookmark(shareUrl)
            onShareHandled()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.87f),
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            if (navigationState.currentKey !is AddBookmarkKey && navigationState.currentKey !is EditBookmarkKey) {
                BottomNavigationBar(
                    currentTopLevelKey = navigationState.currentTopLevelKey,
                    onDestinationClick = navigator::navigate
                )
            }
        }
    ) { innerPadding ->
        Box(Modifier.fillMaxSize().padding(innerPadding)) {
            NavDisplay(
                entries = navigationState.toEntries(entries),
                onBack = { navigator.goBack() },
                transitionSpec = { EnterTransition.None togetherWith ExitTransition.None },
                popTransitionSpec = { EnterTransition.None togetherWith ExitTransition.None },
                predictivePopTransitionSpec = { _ -> EnterTransition.None togetherWith ExitTransition.None }
            )
        }
    }
}
