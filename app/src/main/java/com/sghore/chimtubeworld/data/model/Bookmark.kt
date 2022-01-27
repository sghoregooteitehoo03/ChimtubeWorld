package com.sghore.chimtubeworld.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Bookmark(
    @PrimaryKey(autoGenerate = true) val id: Int? = null,
    @ColumnInfo(name = "video_id") val videoId: String,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "video_position") val videoPosition: Long,
    @ColumnInfo(name = "color") val color: Int
) {
    override fun equals(other: Any?): Boolean {
        other as Bookmark?
        return other?.id == id
    }
}