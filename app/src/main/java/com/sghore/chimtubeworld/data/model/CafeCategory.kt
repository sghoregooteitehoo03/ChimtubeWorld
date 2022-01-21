package com.sghore.chimtubeworld.data.model

data class CafeCategory(
    val category: String,
    val categoryId: Int
) {
    var isSelected = false

    override fun equals(other: Any?): Boolean {
        other as CafeCategory
        return categoryId == other.categoryId
    }
}
