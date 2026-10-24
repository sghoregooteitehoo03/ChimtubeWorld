package com.sghore.chimtubeworld.core.data.repository

import com.sghore.chimtubeworld.core.data.datasource.FakeCafeRemoteDataSource
import com.sghore.chimtubeworld.core.data.datasource.SpyDao
import com.sghore.chimtubeworld.core.database.entity.ReadHistoryEntity
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

class CafeRepositoryImplTest {

    @Test
    fun `읽음 기록이 없을 때 요청한 게시글의 기록만 저장한다`() = runTest {
        val dao = SpyDao().apply { readData = listOf(ReadHistoryEntity(id = 3, articleId = 73)) }
        val remote = FakeCafeRemoteDataSource()
        val repository = CafeRepositoryImpl(dao, remote, StandardTestDispatcher(testScheduler))

        repository.readPost(articleId = 42)

        assertEquals(listOf(42), dao.readRequests)
        assertEquals(listOf(ReadHistoryEntity(articleId = 42)), dao.readInsertAttempts)
        assertEquals(listOf(ReadHistoryEntity(articleId = 42)), dao.savedReadData)
        assertTrue(remote.requests.isEmpty())
    }

    @Test
    fun `읽음 기록이 이미 있으면 다시 저장하지 않는다`() = runTest {
        val dao = SpyDao().apply { readData = listOf(ReadHistoryEntity(id = 7, articleId = 42)) }
        val repository = CafeRepositoryImpl(dao, FakeCafeRemoteDataSource(), StandardTestDispatcher(testScheduler))

        repository.readPost(articleId = 42)

        assertEquals(listOf(42), dao.readRequests)
        assertTrue(dao.readInsertAttempts.isEmpty())
        assertTrue(dao.savedReadData.isEmpty())
    }

    @Test
    fun `읽음 조회 실패 시 저장하지 않고 예외를 전달한다`() = runTest {
        val failure = IllegalStateException("조회 실패")
        val dao = SpyDao().apply { getReadFailure = failure }
        val repository = CafeRepositoryImpl(dao, FakeCafeRemoteDataSource(), StandardTestDispatcher(testScheduler))

        assertSame(failure, runCatching { repository.readPost(42) }.exceptionOrNull())
        assertEquals(listOf(42), dao.readRequests)
        assertTrue(dao.readInsertAttempts.isEmpty())
        assertTrue(dao.savedReadData.isEmpty())
    }

    @Test
    fun `읽음 저장 실패 시 저장 시도를 기록하고 예외를 전달한다`() = runTest {
        val failure = IllegalStateException("저장 실패")
        val dao = SpyDao().apply { insertReadFailure = failure }
        val repository = CafeRepositoryImpl(dao, FakeCafeRemoteDataSource(), StandardTestDispatcher(testScheduler))

        assertSame(failure, runCatching { repository.readPost(42) }.exceptionOrNull())
        assertEquals(listOf(42), dao.readRequests)
        assertEquals(listOf(ReadHistoryEntity(articleId = 42)), dao.readInsertAttempts)
        assertTrue(dao.savedReadData.isEmpty())
    }
}
