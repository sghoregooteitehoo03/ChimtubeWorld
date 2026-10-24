package com.sghore.chimtubeworld.core.testing.repository

import androidx.paging.PagingData
import com.sghore.chimtubeworld.core.domain.repository.YoutubeRepository
import com.sghore.chimtubeworld.core.model.Channel
import com.sghore.chimtubeworld.core.model.Playlist
import com.sghore.chimtubeworld.core.model.Video
import com.sghore.chimtubeworld.core.testing.model.video
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf

/** 실제 저장소 없이 메서드별 응답과 지연을 제어하고 요청 인자를 기록한다. */
class FakeYoutubeRepository(
    initialVideo: Video = video(),
    initialChannels: List<Channel?> = emptyList()
) : YoutubeRepository {
    data class MainPlaylistRequest(val channelId: String?, val playlistId: List<String>)

    private var videosResult: Result<Flow<PagingData<Video>>> = Result.success(flowOf(PagingData.from(emptyList<Video>())))
    private val _videosRequests = mutableListOf<String>()
    val videosRequests: List<String>
        get() = _videosRequests.toList()

    var videosResponseDelayMillis: Long = 0L
        set(value) {
            require(value >= 0L) { "응답 지연은 0 이상이어야 합니다." }
            field = value
        }

    fun setVideosResult(value: Flow<PagingData<Video>>) {
        videosResult = Result.success(value)
    }

    fun setVideosResult(items: List<Video>) {
        setVideosResult(flowOf(PagingData.from(items.toList())))
    }

    fun setVideosError(throwable: Throwable) {
        videosResult = Result.failure(throwable)
    }

    override fun getVideos(channelId: String): Flow<PagingData<Video>> {
        _videosRequests += channelId

        val response = videosResult
        val delayMillis = videosResponseDelayMillis

        // Flow를 수집하기 전 설정이 바뀌어도 호출 당시 응답을 사용한다.
        return flow {
            delay(delayMillis)
            emitAll(response.getOrThrow())
        }
    }

    private var mainPlaylistResult: Result<Flow<PagingData<Playlist>>> = Result.success(flowOf(PagingData.from(emptyList<Playlist>())))
    private val _mainPlaylistRequests = mutableListOf<MainPlaylistRequest>()
    val mainPlaylistRequests: List<MainPlaylistRequest>
        get() = _mainPlaylistRequests.toList()

    var mainPlaylistResponseDelayMillis: Long = 0L
        set(value) {
            require(value >= 0L) { "응답 지연은 0 이상이어야 합니다." }
            field = value
        }

    fun setMainPlaylistResult(value: Flow<PagingData<Playlist>>) {
        mainPlaylistResult = Result.success(value)
    }

    fun setMainPlaylistResult(items: List<Playlist>) {
        setMainPlaylistResult(flowOf(PagingData.from(items.toList())))
    }

    fun setMainPlaylistError(throwable: Throwable) {
        mainPlaylistResult = Result.failure(throwable)
    }

    override fun getPagingMainPlaylist(channelId: String?, playlistId: List<String>): Flow<PagingData<Playlist>> {
        _mainPlaylistRequests += MainPlaylistRequest(channelId, playlistId.toList())

        val response = mainPlaylistResult
        val delayMillis = mainPlaylistResponseDelayMillis

        // Flow를 수집하기 전 설정이 바뀌어도 호출 당시 응답을 사용한다.
        return flow {
            delay(delayMillis)
            emitAll(response.getOrThrow())
        }
    }

    private var subPlaylistResult: Result<Flow<PagingData<Playlist>>> = Result.success(flowOf(PagingData.from(emptyList<Playlist>())))
    private val _subPlaylistRequests = mutableListOf<List<String>>()
    val subPlaylistRequests: List<List<String>>
        get() = _subPlaylistRequests.toList()

    var subPlaylistResponseDelayMillis: Long = 0L
        set(value) {
            require(value >= 0L) { "응답 지연은 0 이상이어야 합니다." }
            field = value
        }

    fun setSubPlaylistResult(value: Flow<PagingData<Playlist>>) {
        subPlaylistResult = Result.success(value)
    }

    fun setSubPlaylistResult(items: List<Playlist>) {
        setSubPlaylistResult(flowOf(PagingData.from(items.toList())))
    }

    fun setSubPlaylistError(throwable: Throwable) {
        subPlaylistResult = Result.failure(throwable)
    }

    override fun getPagingSubPlaylist(playlistId: List<String>): Flow<PagingData<Playlist>> {
        _subPlaylistRequests += listOf(playlistId.toList())

        val response = subPlaylistResult
        val delayMillis = subPlaylistResponseDelayMillis

        // Flow를 수집하기 전 설정이 바뀌어도 호출 당시 응답을 사용한다.
        return flow {
            delay(delayMillis)
            emitAll(response.getOrThrow())
        }
    }

    private var channelsResult: Result<List<Channel?>> = Result.success(initialChannels.toList())
    private var _channelsCallCount = 0
    val channelsCallCount: Int
        get() = _channelsCallCount

    var channelsResponseDelayMillis: Long = 0L
        set(value) {
            require(value >= 0L) { "응답 지연은 0 이상이어야 합니다." }
            field = value
        }

    fun setChannelsResult(value: List<Channel?>) {
        channelsResult = Result.success(value.toList())
    }

    fun setChannelsError(throwable: Throwable) {
        channelsResult = Result.failure(throwable)
    }

    override suspend fun getChannels(): List<Channel?> {
        _channelsCallCount++

        val response = channelsResult
        val delayMillis = channelsResponseDelayMillis

        delay(delayMillis)

        return response.getOrThrow()
    }

    private var videoResult: Result<Video> = Result.success(initialVideo)
    private val _videoRequests = mutableListOf<String>()
    val videoRequests: List<String>
        get() = _videoRequests.toList()

    var videoResponseDelayMillis: Long = 0L
        set(value) {
            require(value >= 0L) { "응답 지연은 0 이상이어야 합니다." }
            field = value
        }

    fun setVideoResult(value: Video) {
        videoResult = Result.success(value)
    }

    fun setVideoError(throwable: Throwable) {
        videoResult = Result.failure(throwable)
    }

    override suspend fun getVideo(url: String): Video {
        _videoRequests += url

        val response = videoResult
        val delayMillis = videoResponseDelayMillis

        delay(delayMillis)

        return response.getOrThrow()
    }
}
