package com.sghore.chimtubeworld.viewmodel.webToonFrag

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sghore.chimtubeworld.data.Channel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import javax.inject.Inject

@HiltViewModel
class WebToonViewModel @Inject constructor(
    private val repository: WebToonRepository
) : ViewModel() {
    private val _webToonList = MutableLiveData<List<Channel>?>(null)
    val webToonList: LiveData<List<Channel>?> = _webToonList

    fun getWebToonList() = viewModelScope.launch {

        _webToonList.value = CoroutineScope(Dispatchers.IO).async {
            repository.getWebToonList()
        }.await()
    }
}