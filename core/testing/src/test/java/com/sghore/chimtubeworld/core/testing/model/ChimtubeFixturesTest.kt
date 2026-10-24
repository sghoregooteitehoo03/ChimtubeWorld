package com.sghore.chimtubeworld.core.testing.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotSame
import org.junit.Assert.assertTrue
import org.junit.Test

class ChimtubeFixturesTest {

    @Test
    fun `각 fixture는 호출마다 새 모델을 만든다`() {
        assertNotSame(channel(), channel())
        assertNotSame(video(), video())
        assertNotSame(playlist(), playlist())
        assertNotSame(post(), post())
        assertNotSame(bookmark(), bookmark())
        assertNotSame(linkInfo(), linkInfo())
        assertNotSame(cafeCategory(), cafeCategory())
        assertNotSame(goodsChannelInfo(), goodsChannelInfo())
    }

    @Test
    fun `가변 모델과 배열 및 입력 목록을 호출 간 공유하지 않는다`() {
        val explains = arrayOf("원본 설명")
        val firstChannel = channel(explains = explains)
        explains[0] = "변경"
        assertEquals("원본 설명", firstChannel.explains.single())
        firstChannel.explains[0] = "채널 변경"
        assertEquals("채널 설명", channel().explains.single())

        val firstPost = post().apply { isRead = true }
        assertTrue(firstPost.isRead)
        assertFalse(post().isRead)

        val bookmarks = mutableListOf(bookmark())
        val firstVideo = video(bookmarks = bookmarks)
        bookmarks.clear()
        assertEquals(1, firstVideo.bookmarks.size)
        firstVideo.bookmarks = emptyList()
        assertTrue(video().bookmarks.isEmpty())
    }

    @Test
    fun `필요한 필드만 지정하고 나머지는 의미 있는 기본값을 사용한다`() {
        val item = bookmark(id = null, videoId = "custom", videoPosition = 77L)
        assertEquals(null, item.id)
        assertEquals("custom", item.videoId)
        assertEquals(77L, item.videoPosition)
        assertEquals("다시 볼 장면", item.title)
        assertEquals("https://www.youtube.com/watch?v=custom", video(id = "custom").url)
        assertEquals("사용자 제목", post(title = "사용자 제목").title)
        assertEquals("사용자 목록", playlist(title = "사용자 목록").title)
        assertEquals("링크 설명", linkInfo(explain = "링크 설명").explain)
    }
}
