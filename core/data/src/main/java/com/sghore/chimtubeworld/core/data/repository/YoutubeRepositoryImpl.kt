package com.sghore.chimtubeworld.core.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.google.firebase.firestore.FirebaseFirestore
import com.sghore.chimtubeworld.core.network.datasource.YoutubeRemoteDataSource
import com.sghore.chimtubeworld.core.database.dao.Dao
import com.sghore.chimtubeworld.core.model.Channel
import com.sghore.chimtubeworld.core.model.LinkInfo
import com.sghore.chimtubeworld.core.model.Video
import com.sghore.chimtubeworld.core.data.repository.dataSource.MainPlaylistPagingSource
import com.sghore.chimtubeworld.core.data.repository.dataSource.SubPlaylistPagingSource
import com.sghore.chimtubeworld.core.data.repository.dataSource.YoutubePagingSource
import com.sghore.chimtubeworld.core.common.Constants
import com.sghore.chimtubeworld.core.network.dto.youtubeAPI.PlaylistsDTO
import com.sghore.chimtubeworld.core.common.getVideoId
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*
import com.sghore.chimtubeworld.core.domain.repository.YoutubeRepository
import javax.inject.Inject
import kotlin.time.Duration

internal class YoutubeRepositoryImpl @Inject constructor(
    private val store: FirebaseFirestore,
    private val remoteDataSource: YoutubeRemoteDataSource,
    private val dao: Dao,
) : YoutubeRepository {

    // 유튜브 영상을 페이징하여 가져옴
    override fun getVideos(channelId: String) =
        Pager(PagingConfig(10)) {
            YoutubePagingSource(
                channelId = channelId,
                remoteDataSource = remoteDataSource,
                dao = dao
            )
        }.flow

    // 유튜브 재생목록을 가져옴
    private suspend fun getPlaylist(
        channelId: String?,
        playlistId: List<String>?,
        pageToken: String? = null,
    ): PlaylistsDTO {
        return remoteDataSource.getYPlaylists(
            channelId = channelId,
            playlistId = playlistId,
            pageToken = pageToken
        )
    }

    // 메인채널의 재생목록을 페이징하여 가져옴
    override fun getPagingMainPlaylist(
        channelId: String?,
        playlistId: List<String>,
    ) = Pager(PagingConfig(pageSize = 10)) {
        MainPlaylistPagingSource(
            channelId = channelId,
            playlistId = playlistId.toMutableList(),
            getPlaylist = ::getPlaylist
        )
    }.flow

    // 서브채널의 재생목록을 페이징하여 가져옴
    override fun getPagingSubPlaylist(
        playlistId: List<String>,
    ) = Pager(PagingConfig(pageSize = 5)) {
        SubPlaylistPagingSource(
            playlistId = playlistId.toMutableList(),
            getPlaylist = ::getPlaylist
        )
    }.flow

    override suspend fun getChannels(): List<Channel?> {
        val channelLinkData = getChannelLinkData()
        return getChannelInfo(channelLinkData)
    }

    // 채널의 Id 및 API에서 가져오지 못하는 부가설명을 가져옴
    private suspend fun getChannelLinkData() =
        store.collection(Constants.COLLECTION_YOUTUBE_LINK)
            .get()
            .await()
            .map { document ->
                LinkInfo(
                    id = document["id"] as String,
                    url = document["url"] as String,
                    explain = document["explain"] as String,
                    type = (document["type"] as Long).toInt(),
                    playlistId = document["playlistId"] as String,
                    playlistName = document["playlistName"] as String
                )
            }

    // 채널의 정보를 가져옴
    private suspend fun getChannelInfo(channelLinkList: List<LinkInfo>): List<Channel> {
        // API를 통해 채널들의 정보를 가져옴             [0] = 채널아이디, [1] = 플레이리스트 아이디
        val result = remoteDataSource.getYChannelInfo(
            channelId = channelLinkList.map { it.id.split("|")[0] }
                .toTypedArray()
        )
        // 채널 아이디 배열
        val channelIdArr = result.items.map { it.id }

        return channelLinkList.map { linkInfo ->
            // API에서 넘어온 데이터가 linkInfo와 일치하는 데이터의 Index값
            val index = channelIdArr.indexOf(linkInfo.id.split("|")[0])
            val channelInfo = result.items[index]
            val explain = if (linkInfo.type == 0) {
                channelInfo.snippet.description
            } else {
                linkInfo.explain
            }

            Channel(
                id = linkInfo.id,
                playlistId = linkInfo.playlistId,
                playlistName = linkInfo.playlistName,
                name = channelInfo.snippet.title,
                explains = arrayOf(explain),
                url = linkInfo.url,
                image = channelInfo.snippet.thumbnails.medium.url,
                type = linkInfo.type
            )
        }
    }

    // 유튜브에서 영상 정보를 가져옴
    override suspend fun getVideo(url: String): Video {
        val videoId = getVideoId(url) ?: // 오류 발생 시
        throw NullPointerException()

        val videoData = remoteDataSource.getYVideos(arrayOf(videoId))

        return videoData.items[0].let { response ->
            val dateFormat = SimpleDateFormat(
                "yyyy-MM-dd'T'HH:mm:ss'Z'",
                Locale.KOREA
            )
            // 업로드 시간
            val uploadTime =
                dateFormat.parse(response.snippet.publishedAt).time + 32400000 // (+9 Hours) UTC -> KOREA
            // 영상 길이
            val duration = Duration.parse(response.contentDetails.duration)
                .inWholeMilliseconds - 32400000 // (-9 Hours) KOREA -> UTC

            Video(
                id = response.id,
                channelName = response.snippet.channelTitle,
                title = response.snippet.title, // 제목
                thumbnail = response.snippet.thumbnails.high.url, // 썸네일 이미지
                uploadTime = uploadTime,
                viewCount = response.statistics.viewCount.toLong(), // 조회수
                duration = duration,
                url = "https://www.youtube.com/watch?v=${response.id}"
            )
        }
    }
}
