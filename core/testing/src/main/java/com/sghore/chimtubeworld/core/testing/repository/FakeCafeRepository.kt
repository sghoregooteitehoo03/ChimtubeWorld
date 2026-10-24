package com.sghore.chimtubeworld.core.testing.repository

import androidx.paging.PagingData
import com.sghore.chimtubeworld.core.domain.repository.CafeRepository
import com.sghore.chimtubeworld.core.model.Channel
import com.sghore.chimtubeworld.core.model.Post
import com.sghore.chimtubeworld.core.testing.model.channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf

/** 실제 저장소 없이 메서드별 응답과 지연을 제어하고 요청 인자를 기록한다. */
class FakeCafeRepository(
    initialInfo: Channel = channel(),
    initialPosts: List<Post> = emptyList()
) : CafeRepository {
    private var postsResult: Result<Flow<PagingData<Post>>> = Result.success(flowOf(PagingData.from(initialPosts.toList())))
    private val _postsRequests = mutableListOf<Int>()
    val postsRequests: List<Int>
        get() = _postsRequests.toList()

    var postsResponseDelayMillis: Long = 0L
        set(value) {
            require(value >= 0L) { "응답 지연은 0 이상이어야 합니다." }
            field = value
        }

    fun setPostsResult(value: Flow<PagingData<Post>>) {
        postsResult = Result.success(value)
    }

    fun setPostsResult(items: List<Post>) {
        setPostsResult(flowOf(PagingData.from(items.toList())))
    }

    fun setPostsError(throwable: Throwable) {
        postsResult = Result.failure(throwable)
    }

    override fun getCafePosts(categoryId: Int): Flow<PagingData<Post>> {
        _postsRequests += categoryId

        val response = postsResult
        val delayMillis = postsResponseDelayMillis

        // Flow를 수집하기 전 설정이 바뀌어도 호출 당시 응답을 사용한다.
        return flow {
            delay(delayMillis)
            emitAll(response.getOrThrow())
        }
    }

    private var infoResult: Result<Channel> = Result.success(initialInfo)
    private var _infoCallCount = 0
    val infoCallCount: Int
        get() = _infoCallCount

    var infoResponseDelayMillis: Long = 0L
        set(value) {
            require(value >= 0L) { "응답 지연은 0 이상이어야 합니다." }
            field = value
        }

    fun setInfoResult(value: Channel) {
        infoResult = Result.success(value)
    }

    fun setInfoError(throwable: Throwable) {
        infoResult = Result.failure(throwable)
    }

    override suspend fun getCafeInfo(): Channel {
        _infoCallCount++

        val response = infoResult
        val delayMillis = infoResponseDelayMillis

        delay(delayMillis)

        return response.getOrThrow()
    }

    private var readResult: Result<Unit> = Result.success(Unit)
    private val _readRequests = mutableListOf<Int>()
    val readRequests: List<Int>
        get() = _readRequests.toList()

    var readResponseDelayMillis: Long = 0L
        set(value) {
            require(value >= 0L) { "응답 지연은 0 이상이어야 합니다." }
            field = value
        }

    fun setReadResult() {
        readResult = Result.success(Unit)
    }

    fun setReadError(throwable: Throwable) {
        readResult = Result.failure(throwable)
    }

    override suspend fun readPost(articleId: Int): Unit {
        _readRequests += articleId

        val response = readResult
        val delayMillis = readResponseDelayMillis

        delay(delayMillis)

        return response.getOrThrow()
    }
}
