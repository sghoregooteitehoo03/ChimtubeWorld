package com.sghore.chimtubeworld.presentation.cafeScreen

import androidx.lifecycle.*
import androidx.paging.cachedIn
import com.sghore.chimtubeworld.core.model.Post
import com.sghore.chimtubeworld.core.model.Channel
import com.sghore.chimtubeworld.core.common.Resource
import com.sghore.chimtubeworld.core.common.suspendRunCatching
import com.sghore.chimtubeworld.core.domain.repository.CafeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CafeViewModel @Inject constructor(
    private val repository: CafeRepository
) : ViewModel() {
    private val _state = MutableStateFlow(CafeScreenState())
    val state: StateFlow<CafeScreenState> = _state.asStateFlow()

    init {
        getCafeInfo()
    }

    private fun loadCafeInfo(): Flow<Resource<Channel>> = flow {
        suspendRunCatching {
            val cafeInfo = repository.getCafeInfo()
            emit(Resource.Success<Channel>(cafeInfo))
        }.onFailure { e ->
            if (e !is Exception) throw e
            e.printStackTrace()
            emit(Resource.Error<Channel>("데이터를 불러오는데 오류가 발생하였습니다."))
        }
    }

    // 카페의 정보를 가져옴
    fun getCafeInfo() {
        loadCafeInfo().onEach { resource ->
            when (resource) {
                is Resource.Success -> {
                    _state.update {
                        CafeScreenState(
                            cafeInfo = resource.data,
                            cafePosts = repository.getCafePosts(categoryId = 0)
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
                cafePosts = repository.getCafePosts(categoryId = categoryId)
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

            suspendRunCatching {
                repository.readPost(post.id)
            }.onFailure { e ->
                if (e !is Exception) throw e
                e.printStackTrace()
            }
        }
    }
}
