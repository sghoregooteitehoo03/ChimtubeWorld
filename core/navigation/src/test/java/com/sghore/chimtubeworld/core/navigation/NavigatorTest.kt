package com.sghore.chimtubeworld.core.navigation

import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class NavigatorTest {
    @Test
    fun navigatingToAnotherTopLevelDestinationKeepsItsSubStack() {
        val fixture = createFixture()
        fixture.navigator.navigate(fixture.videos)
        fixture.navigator.navigate(Chzzk)
        fixture.navigator.navigate(Youtube)
        assertEquals(Youtube, fixture.state.currentTopLevelKey)
        assertEquals(fixture.videos, fixture.state.currentKey)
    }

    @Test
    fun reselectingCurrentTopLevelDestinationClearsItsSubStack() {
        val fixture = createFixture()
        fixture.navigator.navigate(fixture.playlists)
        fixture.navigator.navigate(fixture.videos)
        fixture.navigator.navigate(Youtube)
        assertEquals(listOf(Youtube), fixture.state.currentSubStack)
    }

    @Test
    fun goBackPopsChildThenPreviousTopLevelDestinationAndStopsAtStart() {
        val fixture = createFixture()
        fixture.navigator.navigate(fixture.playlists)
        fixture.navigator.navigate(fixture.videos)
        assertTrue(fixture.navigator.goBack())
        assertEquals(fixture.playlists, fixture.state.currentKey)
        assertTrue(fixture.navigator.goBack())
        assertEquals(Youtube, fixture.state.currentKey)
        fixture.navigator.navigate(Chzzk)
        assertTrue(fixture.navigator.goBack())
        assertEquals(Youtube, fixture.state.currentTopLevelKey)
        assertFalse(fixture.navigator.goBack())
    }

    @Test
    fun fiveTabsReturnInReverseVisitOrder() {
        val fixture = createFixture()
        listOf(Chzzk, Webtoon, Cafe, Store).forEach(fixture.navigator::navigate)
        listOf(Cafe, Webtoon, Chzzk, Youtube).forEach { expected ->
            assertTrue(fixture.navigator.goBack())
            assertEquals(expected, fixture.state.currentTopLevelKey)
        }
        assertFalse(fixture.navigator.goBack())
    }

    @Test
    fun returningToStartTabClearsTabHistoryButPreservesItsChildren() {
        val fixture = createFixture()
        fixture.navigator.navigate(fixture.playlists)
        listOf(Chzzk, Webtoon, Cafe, Store, Youtube).forEach(fixture.navigator::navigate)
        assertEquals(listOf(Youtube), fixture.state.topLevelStack)
        assertEquals(fixture.playlists, fixture.state.currentKey)
        assertTrue(fixture.navigator.goBack())
        assertFalse(fixture.navigator.goBack())
    }

    @Test
    fun revisitingTabMovesItToEndWithoutDuplicatingTabHistory() {
        val fixture = createFixture()
        listOf(Chzzk, Webtoon, Cafe, Store, Chzzk).forEach(fixture.navigator::navigate)
        assertEquals(listOf(Youtube, Webtoon, Cafe, Store, Chzzk), fixture.state.topLevelStack)
        assertTrue(fixture.navigator.goBack())
        assertEquals(Store, fixture.state.currentTopLevelKey)
    }

    @Test
    fun reselectingAnyTabClearsOnlyThatTabsChildren() {
        val fixture = createFixture()
        fixture.state.topLevelKeys.forEach { tab ->
            fixture.navigator.navigate(tab)
            fixture.navigator.navigate(TestNavKey("child-$tab"))
        }
        fixture.state.topLevelKeys.forEach { tab ->
            fixture.navigator.navigate(tab)
            assertEquals(2, fixture.state.currentSubStack.size)
            fixture.navigator.navigate(tab)
            assertEquals(listOf(tab), fixture.state.currentSubStack)
        }
    }

    @Test
    fun popUpToOnlyChangesCurrentSubStack() {
        val fixture = createFixture()
        fixture.navigator.navigate(fixture.playlists)
        fixture.navigator.navigate(fixture.videos)
        fixture.navigator.navigate(Cafe)
        fixture.navigator.navigate(TestNavKey("cafe-child"))
        fixture.navigator.popUpTo(fixture.playlists)
        assertEquals(2, fixture.state.currentSubStack.size)
        fixture.navigator.navigate(Youtube)
        fixture.navigator.popUpTo(fixture.playlists)
        assertEquals(listOf(Youtube, fixture.playlists), fixture.state.currentSubStack)
        fixture.navigator.navigate(fixture.videos, popUpTo = fixture.playlists, inclusive = true)
        assertEquals(listOf(Youtube, fixture.videos), fixture.state.currentSubStack)
        assertEquals(2, fixture.state.subStacks.getValue(Cafe).size)
    }

    @Test
    fun shareAndEditReturnToVideosAndResultsAreConsumedOnce() {
        val fixture = createFixture()
        fixture.navigator.navigate(fixture.playlists)
        fixture.navigator.navigate(fixture.videos)
        listOf("add-bookmark", "edit-bookmark").forEach { destination ->
            fixture.navigator.navigate(TestNavKey(destination))
            fixture.navigator.setResult(BookmarkResult, destination)
            assertTrue(fixture.navigator.goBack())
            assertEquals(fixture.videos, fixture.state.currentKey)
            assertEquals(destination, fixture.navigator.consumeResult(BookmarkResult))
            assertNull(fixture.navigator.consumeResult(BookmarkResult))
        }
    }

    @Test
    fun shareFromEveryTabReturnsToItsPreviousScreen() {
        val fixture = createFixture()
        fixture.state.topLevelKeys.forEach { tab ->
            fixture.navigator.navigate(tab)
            val previous = fixture.state.currentKey
            fixture.navigator.navigate(TestNavKey("add-bookmark"))
            assertEquals(tab, fixture.state.currentTopLevelKey)
            assertTrue(fixture.navigator.goBack())
            assertEquals(previous, fixture.state.currentKey)
        }
    }

    @Test
    fun resultKeysAndNavigationStatesDoNotShareResults() {
        val fixture = createFixture()
        fixture.navigator.setResult(BookmarkResult, "bookmark")
        fixture.navigator.setResult(OtherResult, 42)
        assertNull(createFixture().navigator.consumeResult(BookmarkResult))
        assertEquals("bookmark", fixture.navigator.consumeResult(BookmarkResult))
        assertEquals(42, fixture.navigator.consumeResult(OtherResult))
    }

    private fun createFixture(): NavigationFixture {
        val state = NavigationState(
            startKey = Youtube,
            topLevelStack = NavBackStack<NavKey>(Youtube),
            subStacks = listOf(Youtube, Chzzk, Webtoon, Cafe, Store).associateWith { NavBackStack<NavKey>(it) }
        )
        return NavigationFixture(state, Navigator(state))
    }

    private data class NavigationFixture(
        val state: NavigationState,
        val navigator: Navigator,
        val playlists: NavKey = TestNavKey("playlists"),
        val videos: NavKey = TestNavKey("videos")
    )

    private data class TestNavKey(val value: String) : NavKey

    private object BookmarkResult : NavigationResultKey<String>()
    private object OtherResult : NavigationResultKey<Int>()

    private companion object {
        val Youtube: NavKey = TestNavKey("youtube")
        val Chzzk: NavKey = TestNavKey("chzzk")
        val Webtoon: NavKey = TestNavKey("webtoon")
        val Cafe: NavKey = TestNavKey("cafe")
        val Store: NavKey = TestNavKey("store")
    }
}
