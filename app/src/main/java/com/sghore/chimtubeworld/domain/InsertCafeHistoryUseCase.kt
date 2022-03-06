package com.sghore.chimtubeworld.domain

import com.sghore.chimtubeworld.data.repository.CafeRepository
import javax.inject.Inject

class InsertCafeHistoryUseCase @Inject constructor(
    private val repository: CafeRepository
) {
    suspend operator fun invoke(postId: Int) {
        try {
            repository.readPost(postId)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}