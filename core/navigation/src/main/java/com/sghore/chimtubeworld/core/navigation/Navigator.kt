package com.sghore.chimtubeworld.core.navigation

import androidx.navigation3.runtime.NavKey

/*
 * feature API는 자신이 소유한 NavKey와 이 class의 확장 함수로 feature 전용 이동 API를 정의한다.
 * navigation 모듈은 feature 이름이나 route 파라미터를 알지 않고 top-level 및 하위 back stack 조작과 결과 전달만 제공한다.
 */
class Navigator(val state: NavigationState) {
    fun <T : Any> setResult(key: NavigationResultKey<T>, value: T) {
        state.setResult(key, value)
    }

    fun <T : Any> consumeResult(key: NavigationResultKey<T>): T? {
        return state.consumeResult(key)
    }

    fun navigate(key: NavKey) {
        when {
            key == state.currentTopLevelKey -> clearCurrentSubStack()
            key in state.topLevelKeys -> selectTopLevel(key)
            else -> state.currentSubStack.add(key)
        }
    }

    fun navigate(
        key: NavKey,
        popUpTo: NavKey,
        inclusive: Boolean
    ) {
        popUpTo(popUpTo, inclusive)
        navigate(key)
    }

    fun goBack(): Boolean {
        return when {
            state.currentKey != state.currentTopLevelKey -> {
                state.currentSubStack.removeAt(state.currentSubStack.lastIndex)
                true
            }

            state.currentTopLevelKey != state.startKey && state.topLevelStack.size > 1 -> {
                state.topLevelStack.removeAt(state.topLevelStack.lastIndex)
                true
            }

            else -> false
        }
    }

    fun popUpTo(key: NavKey, inclusive: Boolean = false) {
        val backStack = state.currentSubStack
        val index = backStack.indexOf(key)
        if (index == -1) return

        val clearFrom = if (inclusive) index else index + 1
        if (clearFrom < backStack.size) {
            backStack.subList(clearFrom, backStack.size).clear()
        }
    }

    private fun selectTopLevel(key: NavKey) {
        state.topLevelStack.apply {
            if (key == state.startKey) {
                clear()
            } else {
                remove(key)
            }
            add(key)
        }
    }

    private fun clearCurrentSubStack() {
        val subStack = state.currentSubStack
        if (subStack.size > 1) {
            subStack.subList(1, subStack.size).clear()
        }
    }
}
