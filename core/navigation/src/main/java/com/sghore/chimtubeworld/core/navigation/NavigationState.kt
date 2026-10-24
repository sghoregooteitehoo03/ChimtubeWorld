package com.sghore.chimtubeworld.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberDecoratedNavEntries
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator

/*
 * Navigation 3의 top-level stack과 탭별 sub-stack을 관리한다.
 * feature route의 정의나 화면 조합은 포함하지 않고, app이 필요한 방식으로 이 상태를 사용한다.
 */
class NavigationState(
    val startKey: NavKey,
    val topLevelStack: NavBackStack<NavKey>,
    val subStacks: Map<NavKey, NavBackStack<NavKey>>
) {
    private val savedResults = mutableStateMapOf<NavigationResultKey<*>, Any>()

    // 현재 선택된 bottom navigation 탭이다.
    val currentTopLevelKey: NavKey by derivedStateOf {
        topLevelStack.lastOrNull() ?: error("Top-level back stack is empty!")
    }

    val topLevelKeys: Set<NavKey>
        get() = subStacks.keys

    // 현재 탭에서 표시 중인 화면 stack이다.
    val currentSubStack: NavBackStack<NavKey>
        get() = subStacks[currentTopLevelKey]
            ?: error("Sub stack is missing for $currentTopLevelKey")

    // 현재 화면은 선택된 탭 sub-stack의 마지막 key다.
    val currentKey: NavKey by derivedStateOf {
        currentSubStack.lastOrNull() ?: error("Current sub stack is empty!")
    }

    internal fun <T : Any> setResult(key: NavigationResultKey<T>, value: T) {
        savedResults[key] = value
    }

    internal fun <T : Any> consumeResult(key: NavigationResultKey<T>): T? {
        @Suppress("UNCHECKED_CAST")
        return savedResults.remove(key) as? T
    }
}

// app 조립 단계에서는 이 함수로 시작 key와 top-level key를 가진 NavigationState를 생성한다.
@Composable
fun rememberNavigationState(
    startKey: NavKey,
    topLevelKeys: Set<NavKey>
): NavigationState {
    require(startKey in topLevelKeys) {
        "The start key must be a top-level key."
    }

    val topLevelStack = rememberNavBackStack(startKey)
    val subStacks = topLevelKeys.associateWith { key ->
        rememberNavBackStack(key)
    }

    return remember(startKey, topLevelKeys) {
        NavigationState(
            startKey = startKey,
            topLevelStack = topLevelStack,
            subStacks = subStacks
        )
    }
}

/*
 * NavDisplay에 전달할 entry 목록을 만들고 화면별 SaveableState와 ViewModelStore를 유지한다.
 * route별 NavEntry 생성 책임은 feature와 app 조립 계층에 둔다.
 */
@Composable
fun NavigationState.toEntries(
    entryProvider: (NavKey) -> NavEntry<NavKey>
): SnapshotStateList<NavEntry<NavKey>> {
    val decoratedEntries = subStacks.mapValues { (_, subStack) ->
        val decorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator<NavKey>(),
            rememberViewModelStoreNavEntryDecorator<NavKey>()
        )

        rememberDecoratedNavEntries(
            backStack = subStack,
            entryDecorators = decorators,
            entryProvider = entryProvider
        )
    }

    return topLevelStack
        .flatMap { key -> decoratedEntries[key].orEmpty() }
        .toMutableStateList()
}
