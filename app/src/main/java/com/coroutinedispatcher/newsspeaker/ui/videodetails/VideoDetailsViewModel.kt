package com.coroutinedispatcher.newsspeaker.ui.videodetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.coroutinedispatcher.newsspeaker.database.Project
import com.coroutinedispatcher.newsspeaker.usecase.GetCurrentProjectUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VideoDetailsViewModel @Inject constructor(
    private val getCurrentProjectUseCase: GetCurrentProjectUseCase
) : ViewModel() {
    private val _state = MutableStateFlow<State>(State.Loading)
    val state = _state.asStateFlow()

    sealed class State {
        object Loading : State()
        data class Success(val project: Project) : State()
    }

    fun loadProject(pId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val project = getCurrentProjectUseCase(pId)
            _state.update {
                State.Success(project)
            }
        }
    }
}