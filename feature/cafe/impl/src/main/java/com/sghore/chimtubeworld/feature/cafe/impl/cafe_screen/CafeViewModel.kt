package com.sghore.chimtubeworld.feature.cafe.impl.cafe_screen

import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.sghore.chimtubeworld.core.common.Constants
import com.sghore.chimtubeworld.core.common.suspendRunCatching
import com.sghore.chimtubeworld.core.domain.repository.CafeRepository
import com.sghore.chimtubeworld.core.model.Post
import com.sghore.chimtubeworld.core.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class CafeViewModel @Inject constructor(
    private val repository: CafeRepository
) : BaseViewModel<CafeUiState, CafeUiEvent, CafeUiSideEffect>(CafeUiState()) {

    init {
        loadCafeInfo()
    }

    override fun onEvent(event: CafeUiEvent) {
        when (event) {
            is CafeUiEvent.CategoryClicked -> changeCategory(event.categoryId)
            is CafeUiEvent.PostClicked -> openPost(event.post)
            is CafeUiEvent.BannerClicked -> postSideEffect(CafeUiSideEffect.OpenUrl(event.url))
            CafeUiEvent.ChimhahaClicked -> {
                postSideEffect(CafeUiSideEffect.OpenUrl(Constants.CHIMHAHA_URL))
            }
        }
    }

    private fun loadCafeInfo() {
        viewModelScope.launch {
            reduce { copy(isLoading = true, hasLoadError = false) }

            suspendRunCatching {
                repository.getCafeInfo()
            }.onSuccess { cafeInfo ->
                val posts = repository.getCafePosts(categoryId = 0).cachedIn(viewModelScope)

                reduce {
                    CafeUiState(cafeInfo = cafeInfo, cafePosts = posts)
                }
            }.onFailure { error ->
                if (error !is Exception) throw error
                error.printStackTrace()

                reduce { copy(isLoading = false, hasLoadError = true) }
            }
        }
    }

    private fun changeCategory(categoryId: Int) {
        val posts = repository.getCafePosts(categoryId).cachedIn(viewModelScope)

        reduce { copy(cafeCategoryId = categoryId, cafePosts = posts) }
    }

    private fun openPost(post: Post) {
        if (!post.isRead) {
            reduce { copy(readHistory = readHistory + (post.id to true)) }

            viewModelScope.launch {
                suspendRunCatching {
                    repository.readPost(post.id)
                }.onFailure { error ->
                    if (error !is Exception) throw error
                    error.printStackTrace()
                }
            }
        }

        // 읽음 기록 저장 완료를 기다리지 않고 기존처럼 게시글을 연다.
        postSideEffect(CafeUiSideEffect.OpenUrl(post.url ?: ""))
    }
}
