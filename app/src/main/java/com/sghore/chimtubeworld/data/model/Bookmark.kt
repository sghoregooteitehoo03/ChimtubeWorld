package com.sghore.chimtubeworld.data.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity
data class Bookmark(
    @PrimaryKey(autoGenerate = true) val id: Int? = null,
    @ColumnInfo(name = "video_id") val videoId: String,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "video_position") val videoPosition: Long,
    @ColumnInfo(name = "color") val color: Int
) : Parcelable {
    override fun equals(other: Any?): Boolean {
        other as Bookmark?
        return other?.id == id
    }
}