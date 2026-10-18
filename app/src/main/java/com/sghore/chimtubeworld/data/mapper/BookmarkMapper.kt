package com.sghore.chimtubeworld.data.mapper

import com.sghore.chimtubeworld.core.database.entity.BookmarkEntity
import com.sghore.chimtubeworld.core.model.Bookmark

internal fun BookmarkEntity.toDomain(): Bookmark =
    Bookmark(
        id = id,
        videoId = videoId,
        title = title,
        videoPosition = videoPosition,
        color = color
    )

internal fun Bookmark.toEntity(): BookmarkEntity =
    BookmarkEntity(
        id = id,
        videoId = videoId,
        title = title,
        videoPosition = videoPosition,
        color = color
    )
