package com.sghore.chimtubeworld.presentation.cafeScreen

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.*
import androidx.paging.cachedIn
import com.sghore.chimtubeworld.data.model.Resource
import com.sghore.chimtubeworld.domain.GetCafeInfoUseCase
import com.sghore.chimtubeworld.domain.GetCafePostsUseCase
import com.sghore.chimtubeworld.domain.InsertCafeHistoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class CafeViewModel @Inject constructor(
    private val getCafeInfoUseCase: GetCafeInfoUseCase,
    private val getCafePostsUseCase: GetCafePostsUseCase,
    private val insertCafeHistoryUseCase: InsertCafeHistoryUseCase
) : ViewModel() {
    private val _state = mutableStateOf(CafeScreenState())
    val state: State<CafeScreenState> = _state

    init {
        getCafeInfo()
    }

    // 카페의 정보를 가져옴
    fun getCafeInfo() {
        getCafeInfoUseCase().onEach { resource ->
            when (resource) {
                is Resource.Success -> {
                    _state.value = CafeScreenState(
                        cafeInfo = resource.data,
                        cafePosts = getCafePostsUseCase(cafeCategoryId = -1)
                            .cachedIn(viewModelScope)
                    )
                }
                is Resource.Loading -> {}
                is Resource.Error -> {
                    _state.value = CafeScreenState(
                        errorMsg = resource.errorMsg ?: "오류"
                    )
                }
            }
        }.launchIn(CoroutineScope(Dispatchers.IO))
    }

    fun changeCategory(categoryId: Int) {
        _state.value = _state.value.copy(
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
                    _state.value = _state.value.copy(
                        errorMsg = resource.errorMsg ?: ""
                    )
                }
            }
        }.launchIn(CoroutineScope(Dispatchers.IO))
    }
}