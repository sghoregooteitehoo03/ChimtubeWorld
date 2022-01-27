package com.sghore.chimtubeworld.presentation.cafeScreen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.*
import androidx.paging.cachedIn
import com.sghore.chimtubeworld.data.model.Resource
import com.sghore.chimtubeworld.domain.GetCafeInfoUseCase
import com.sghore.chimtubeworld.domain.GetCafePostsUseCase
import com.sghore.chimtubeworld.domain.InsertCafeHistoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class CafeViewModel @Inject constructor(
    private val getCafeInfoUseCase: GetCafeInfoUseCase,
    private val getCafePostsUseCase: GetCafePostsUseCase,
    private val insertCafeHistoryUseCase: InsertCafeHistoryUseCase
) : ViewModel() {
    var state by mutableStateOf(CafeScreenState())
        private set

    init {
        getCafeInfo()
    }

    // 카페의 정보를 가져옴
    fun getCafeInfo() {
        getCafeInfoUseCase().onEach { resource ->
            when (resource) {
                is Resource.Success -> {
                    state = CafeScreenState(
                        cafeInfo = resource.data,
                        cafePosts = getCafePostsUseCase(cafeCategoryId = -1)
                            .cachedIn(viewModelScope)
                    )
                }
                is Resource.Loading -> {}
                is Resource.Error -> {
                    state = CafeScreenState(
                        errorMsg = resource.errorMsg ?: "오류"
                    )
                }
            }
        }.launchIn(viewModelScope)
    }

    fun changeCategory(categoryId: Int) {
        state = state.copy(
            cafeCategoryId = categoryId,
            cafePosts = getCafePostsUseCase(cafeCategoryId = categoryId)
                .cachedIn(viewModelScope)
        )
    }

    // 히스토리 저장
    fun readPost(postId: Int) {
        insertCafeHistoryUseCase(postId).onEach { resource ->
            when (resource) {
                is Resource.Success -> {}
                is Resource.Loading -> {}
                is Resource.Error -> {
                    state = state.copy(
                        errorMsg = resource.errorMsg ?: ""
                    )
                }
            }
        }.launchIn(viewModelScope)
    }
}