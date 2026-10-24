package com.sghore.chimtubeworld.core.testing.repository

import com.sghore.chimtubeworld.core.domain.repository.TwitchRepository
import com.sghore.chimtubeworld.core.model.Channel
import kotlinx.coroutines.delay

/** 실제 저장소 없이 메서드별 응답과 지연을 제어하고 요청 인자를 기록한다. */
class FakeTwitchRepository(
    initialChannels: List<Channel> = emptyList()
) : TwitchRepository {
    private var channelsResult: Result<List<Channel>> = Result.success(initialChannels.toList())
    private var _channelsCallCount = 0
    val channelsCallCount: Int
        get() = _channelsCallCount

    var channelsResponseDelayMillis: Long = 0L
        set(value) {
            require(value >= 0L) { "응답 지연은 0 이상이어야 합니다." }
            field = value
        }

    fun setChannelsResult(value: List<Channel>) {
        channelsResult = Result.success(value.toList())
    }

    fun setChannelsError(throwable: Throwable) {
        channelsResult = Result.failure(throwable)
    }

    override suspend fun getChannels(): List<Channel> {
        _channelsCallCount++

        val response = channelsResult
        val delayMillis = channelsResponseDelayMillis

        delay(delayMillis)

        return response.getOrThrow()
    }
}
