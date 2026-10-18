package com.sghore.chimtubeworld.core.testing.repository

import com.sghore.chimtubeworld.core.domain.repository.BookmarkRepository
import com.sghore.chimtubeworld.core.model.Bookmark
import kotlinx.coroutines.delay

/** 실제 저장소 없이 메서드별 응답과 지연을 제어하고 요청 인자를 기록한다. */
class FakeBookmarkRepository(
    initialItemId: Int = 1
) : BookmarkRepository {
    private var addResult: Result<Unit> = Result.success(Unit)
    private val _addRequests = mutableListOf<Bookmark>()
    val addRequests: List<Bookmark>
        get() = _addRequests.toList()

    var addResponseDelayMillis: Long = 0L
        set(value) {
            require(value >= 0L) { "응답 지연은 0 이상이어야 합니다." }
            field = value
        }

    fun setAddResult() {
        addResult = Result.success(Unit)
    }

    fun setAddError(throwable: Throwable) {
        addResult = Result.failure(throwable)
    }

    override suspend fun addBookmark(bookmark: Bookmark): Unit {
        _addRequests += bookmark

        val response = addResult
        val delayMillis = addResponseDelayMillis

        delay(delayMillis)

        return response.getOrThrow()
    }

    private var deleteResult: Result<Unit> = Result.success(Unit)
    private val _deleteRequests = mutableListOf<Bookmark>()
    val deleteRequests: List<Bookmark>
        get() = _deleteRequests.toList()

    var deleteResponseDelayMillis: Long = 0L
        set(value) {
            require(value >= 0L) { "응답 지연은 0 이상이어야 합니다." }
            field = value
        }

    fun setDeleteResult() {
        deleteResult = Result.success(Unit)
    }

    fun setDeleteError(throwable: Throwable) {
        deleteResult = Result.failure(throwable)
    }

    override suspend fun deleteBookmark(bookmark: Bookmark): Unit {
        _deleteRequests += bookmark

        val response = deleteResult
        val delayMillis = deleteResponseDelayMillis

        delay(delayMillis)

        return response.getOrThrow()
    }

    private var itemIdResult: Result<Int> = Result.success(initialItemId)
    private val _itemIdRequests = mutableListOf<Bookmark>()
    val itemIdRequests: List<Bookmark>
        get() = _itemIdRequests.toList()

    var itemIdResponseDelayMillis: Long = 0L
        set(value) {
            require(value >= 0L) { "응답 지연은 0 이상이어야 합니다." }
            field = value
        }

    fun setItemIdResult(value: Int) {
        itemIdResult = Result.success(value)
    }

    fun setItemIdError(throwable: Throwable) {
        itemIdResult = Result.failure(throwable)
    }

    override suspend fun getItemId(bookmark: Bookmark): Int {
        _itemIdRequests += bookmark

        val response = itemIdResult
        val delayMillis = itemIdResponseDelayMillis

        delay(delayMillis)

        return response.getOrThrow()
    }
}
