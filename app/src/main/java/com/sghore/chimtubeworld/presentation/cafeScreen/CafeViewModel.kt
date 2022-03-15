package com.sghore.chimtubeworld.presentation.cafeScreen

import androidx.lifecycle.*
import androidx.paging.cachedIn
import com.sghore.chimtubeworld.data.model.Post
import com.sghore.chimtubeworld.data.model.Resource
import com.sghore.chimtubeworld.domain.GetCafeInfoUseCase
import com.sghore.chimtubeworld.domain.GetCafePostsUseCase
import com.sghore.chimtubeworld.domain.InsertCafeHistoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CafeViewModel @Inject constructor(
    private val getCafeInfoUseCase: GetCafeInfoUseCase,
    private val getCafePostsUseCase: GetCafePostsUseCase,
    private val insertCafeHistoryUseCase: InsertCafeHistoryUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(CafeScreenState())
    val state: StateFlow<CafeScreenState> = _state.asStateFlow()

    init {
        getCafeInfo()
    }

    // 카페의 정보를 가져옴
    fun getCafeInfo() {
        getCafeInfoUseCase().onEach { resource ->
            when (resource) {
                is Resource.Success -> {
                    _state.update {
                        CafeScreenState(
                            cafeInfo = resource.data,
                            cafePosts = getCafePostsUseCase(cafeCategoryId = -1)
                                .cachedIn(viewModelScope)
                        )
                    }
                }
                is Resource.Loading -> {}
                is Resource.Error -> {
                    _state.update {
                        CafeScreenState(
                            errorMsg = resource.errorMsg ?: "오류"
                        )
                    }
                }
            }
        }.launchIn(viewModelScope)
    }

    // 카테고리 변경
    fun changeCategory(categoryId: Int) {
        _state.update {
            it.copy(
                cafeCategoryId = categoryId,
                cafePosts = getCafePostsUseCase(cafeCategoryId = categoryId)
                    .cachedIn(viewModelScope)
            )
        }
    }

    // 히스토리 저장
    fun readPost(post: Post) = viewModelScope.launch {
        if (!post.isRead) { // 읽지 않은 게시글일 경우
            _state.update {
                it.copy(
                    readHistory = it.readHistory.toMutableMap().apply {
                        this[post.id] = true
                    }
                )
            }

            insertCafeHistoryUseCase(post.id)
        }
    }
}