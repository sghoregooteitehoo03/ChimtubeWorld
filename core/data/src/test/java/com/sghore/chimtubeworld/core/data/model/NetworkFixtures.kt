package com.sghore.chimtubeworld.core.data.model

import com.sghore.chimtubeworld.core.network.dto.naver_cafeAPI.ArticleListDTO
import com.sghore.chimtubeworld.core.network.dto.naver_cafeAPI.ItemDTO
import com.sghore.chimtubeworld.core.network.dto.naver_cafeAPI.WriterInfoDTO
import com.sghore.chimtubeworld.core.network.dto.youtubeAPI.ChannelSnippetDTO
import com.sghore.chimtubeworld.core.network.dto.youtubeAPI.DefaultDTO
import com.sghore.chimtubeworld.core.network.dto.youtubeAPI.PlaylistsItemsDTO
import com.sghore.chimtubeworld.core.network.dto.youtubeAPI.ThumbnailDTO
import com.sghore.chimtubeworld.core.network.dto.youtubeAPI.VideosDetailsDTO
import com.sghore.chimtubeworld.core.network.dto.youtubeAPI.VideosItemDTO
import com.sghore.chimtubeworld.core.network.dto.youtubeAPI.VideosSnippetDTO
import com.sghore.chimtubeworld.core.network.dto.youtubeAPI.VideosStatisticsDTO

internal fun thumbnails(): ThumbnailDTO = ThumbnailDTO(
    default = DefaultDTO("https://example.com/default.jpg"),
    medium = DefaultDTO("https://example.com/medium.jpg"),
    high = DefaultDTO("https://example.com/high.jpg"),
    standard = DefaultDTO("https://example.com/standard.jpg"),
    maxres = DefaultDTO("https://example.com/maxres.jpg")
)

internal fun remoteVideo(id: String = "response-video"): VideosItemDTO = VideosItemDTO(
    id = id,
    snippet = VideosSnippetDTO(
        publishedAt = "2026-01-02T03:04:05Z",
        title = "원격 영상 제목",
        thumbnails = thumbnails(),
        channelTitle = "원격 채널"
    ),
    contentDetails = VideosDetailsDTO(duration = "PT10H2M3S"),
    statistics = VideosStatisticsDTO(viewCount = "987654")
)

internal fun remotePlaylist(id: String = "response-playlist"): PlaylistsItemsDTO = PlaylistsItemsDTO(
    id = id,
    snippet = ChannelSnippetDTO(
        title = "원격 재생목록",
        description = "재생목록 설명",
        publishedAt = "2026-01-02T03:04:05Z",
        thumbnails = thumbnails()
    )
)

internal fun remotePost(
    id: Int = 42,
    headName: String? = "공지",
    representImage: String? = "https://example.com/post.jpg"
): ArticleListDTO = ArticleListDTO(
    ItemDTO(
        articleId = id,
        writerInfo = WriterInfoDTO(nickName = "원격 작성자"),
        menuId = 17,
        headName = headName,
        subject = "원격 게시글",
        representImage = representImage,
        writeDateTimestamp = 1_767_283_445_000L
    )
)
