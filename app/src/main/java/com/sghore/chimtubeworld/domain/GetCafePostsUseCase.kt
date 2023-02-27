package com.sghore.chimtubeworld.domain

import com.sghore.chimtubeworld.data.repository.CafeRepository
import javax.inject.Inject

class GetCafePostsUseCase @Inject constructor(
    private val repository: CafeRepository
) {
    operator fun invoke(cafeCategoryId: Int) =
        repository.getCafePosts(categoryId = cafeCategoryId)
}