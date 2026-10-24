package com.sghore.chimtubeworld.core.testing.rule

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MainDispatcherRuleTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `Main에서 시작한 작업을 runTest와 같은 scheduler로 제어한다`() =
        runTest(mainDispatcherRule.testDispatcher) {
            assertSame(mainDispatcherRule.testDispatcher.scheduler, testScheduler)

            var completed = false
            launch(Dispatchers.Main) {
                delay(1_000L)
                completed = true
            }

            assertFalse(completed)

            advanceUntilIdle()

            assertTrue(completed)
        }
}
