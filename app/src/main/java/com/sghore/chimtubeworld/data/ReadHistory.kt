package com.sghore.chimtubeworld.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ReadHistory(
    @PrimaryKey(autoGenerate = true) val id: Int? = null,
    @ColumnInfo(name = "articleId") val articleId: Int
)