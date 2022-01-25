package com.sghore.chimtubeworld.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.sghore.chimtubeworld.data.model.Bookmark
import com.sghore.chimtubeworld.data.model.Video
import com.sghore.chimtubeworld.data.db.Dao
import com.sghore.chimtubeworld.other.Contents
import com.sghore.chimtubeworld.data.retrofit.RetrofitService
import kotlinx.coroutines.tasks.await
import retrofit2.Retrofit
import retrofit2.await
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.time.Duration

class BookmarkRepository @Inject constructor(
    private val dao: Dao
) {
    // 북마크 추가
    suspend fun addBookmark(bookmark: Bookmark) {
        dao.insertBookmark(bookmark)
    }

    // 북마크 수정
    suspend fun editBookmark(bookmark: Bookmark) {
        dao.updateBookmark(bookmark)
    }

    // 북마크 삭제
    suspend fun deleteBookmark(bookmark: Bookmark) {
        dao.deleteBookmark(bookmark)
    }

    // 북마크의 아이디를 조회함
    suspend fun getItemId(bookmark: Bookmark) =
        dao.getBookmarkItemId(
            videoId = bookmark.videoId,
            title = bookmark.title,
            videoPosition = bookmark.videoPosition,
            color = bookmark.color
        )

}