package com.sghore.chimtubeworld.core.testing.model

import com.sghore.chimtubeworld.core.model.Bookmark
import com.sghore.chimtubeworld.core.model.CafeCategory
import com.sghore.chimtubeworld.core.model.Channel
import com.sghore.chimtubeworld.core.model.GoodsChannelInfo
import com.sghore.chimtubeworld.core.model.LinkInfo
import com.sghore.chimtubeworld.core.model.Playlist
import com.sghore.chimtubeworld.core.model.Post
import com.sghore.chimtubeworld.core.model.Video

// 호출마다 새 모델을 생성하며 필요한 값만 테스트에서 지정한다.
fun channel(
    id: String = "channel-1",
    playlistId: String = "uploads-1",
    playlistName: String = "최근 업로드",
    name: String = "테스트 채널",
    explains: Array<String> = arrayOf("채널 설명"),
    url: String = "https://www.youtube.com/channel/$id",
    image: String = "https://example.com/channel.jpg",
    thumbnailImage: String? = null,
    type: Int = 0,
    isOnline: Boolean? = null
): Channel = Channel(
    id = id,
    playlistId = playlistId,
    playlistName = playlistName,
    name = name,
    explains = explains.copyOf(),
    url = url,
    image = image,
    thumbnailImage = thumbnailImage,
    type = type,
    isOnline = isOnline
)

fun bookmark(
    id: Int? = 1,
    videoId: String = "video-1",
    title: String = "다시 볼 장면",
    videoPosition: Long = 12_000L,
    color: Int = 0x336699
): Bookmark = Bookmark(
    id = id,
    videoId = videoId,
    title = title,
    videoPosition = videoPosition,
    color = color
)

fun video(
    id: String = "video-1",
    channelName: String = "테스트 채널",
    title: String = "테스트 영상",
    thumbnail: String = "https://example.com/video.jpg",
    uploadTime: Long = 1_700_000_000_000L,
    viewCount: Long = 1_234L,
    duration: Long = 60_000L,
    url: String = "https://www.youtube.com/watch?v=$id",
    bookmarks: List<Bookmark> = emptyList()
): Video = Video(
    id = id,
    channelName = channelName,
    title = title,
    thumbnail = thumbnail,
    uploadTime = uploadTime,
    viewCount = viewCount,
    duration = duration,
    url = url,
    bookmarks = bookmarks.toList()
)

fun playlist(
    id: String = "playlist-1",
    title: String = "테스트 재생목록",
    thumbnailImage: String = "https://example.com/playlist.jpg"
): Playlist = Playlist(id = id, title = title, thumbnailImage = thumbnailImage)

fun post(
    id: Int = 42,
    title: String = "테스트 게시글",
    userName: String = "테스트 작성자",
    postDate: String = "2026.10.18.",
    postImage: String = "https://example.com/post.jpg",
    url: String? = "https://cafe.naver.com/test/$id",
    isRead: Boolean = false
): Post = Post(
    id = id,
    title = title,
    userName = userName,
    postDate = postDate,
    postImage = postImage,
    url = url,
    isRead = isRead
)

fun linkInfo(
    id: String = "channel-1",
    url: String = "https://www.youtube.com/channel/$id",
    explain: String = "테스트 링크 설명",
    type: Int = 0,
    playlistId: String = "uploads-1",
    playlistName: String = "최근 업로드"
): LinkInfo = LinkInfo(
    id = id,
    url = url,
    explain = explain,
    type = type,
    playlistId = playlistId,
    playlistName = playlistName
)

fun cafeCategory(
    category: String = "전체글",
    categoryId: Int = 0
): CafeCategory = CafeCategory(category = category, categoryId = categoryId)

fun goodsChannelInfo(
    channelImage: Int = 1,
    channelName: String = "테스트 스토어",
    url: String = "https://example.com/store"
): GoodsChannelInfo = GoodsChannelInfo(
    channelImage = channelImage,
    channelName = channelName,
    url = url
)
