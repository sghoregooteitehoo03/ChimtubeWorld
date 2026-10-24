package com.sghore.chimtubeworld.core.data.mapper

import com.sghore.chimtubeworld.core.database.entity.BookmarkEntity
import com.sghore.chimtubeworld.core.testing.model.bookmark
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotSame
import org.junit.Test

class BookmarkMapperTest {

    @Test
    fun `Entity의 모든 필드를 새 모델로 변환한다`() {
        val entity = BookmarkEntity(42, "response-video", "응답 제목", 98_765L, 0xABCDEF)

        val actual = entity.toDomain()

        // Bookmark.equals는 ID만 비교하므로 각 필드를 별도로 확인한다.
        assertEquals(42, actual.id)
        assertEquals("response-video", actual.videoId)
        assertEquals("응답 제목", actual.title)
        assertEquals(98_765L, actual.videoPosition)
        assertEquals(0xABCDEF, actual.color)
        assertNotSame(actual, entity.toDomain())
    }

    @Test
    fun `자동 생성용 null ID와 모든 모델 필드를 왕복 변환해 보존한다`() {
        val item = bookmark(id = null, videoId = "new-video", title = "새 북마크", videoPosition = 5_432L, color = -123)

        val entity = item.toEntity()
        val restored = entity.toDomain()

        assertEquals(BookmarkEntity(null, "new-video", "새 북마크", 5_432L, -123), entity)
        assertEquals(null, restored.id)
        assertEquals(item.videoId, restored.videoId)
        assertEquals(item.title, restored.title)
        assertEquals(item.videoPosition, restored.videoPosition)
        assertEquals(item.color, restored.color)
        assertNotSame(item, restored)
    }
}
