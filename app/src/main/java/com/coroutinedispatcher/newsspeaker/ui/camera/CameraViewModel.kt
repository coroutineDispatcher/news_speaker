package com.coroutinedispatcher.newsspeaker.ui.camera

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.coroutinedispatcher.newsspeaker.database.Project
import com.coroutinedispatcher.newsspeaker.usecase.GetCurrentProjectUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class CameraViewModel @Inject constructor(
    private val getCurrentProjectUseCase: GetCurrentProjectUseCase
) : ViewModel() {
    private lateinit var currentProject: Project
    private val _state = MutableStateFlow<State>(State.Idle)
    val state = _state.asStateFlow()
    private val _scrollingState = MutableSharedFlow<Int?>()
    val scrollingState = _scrollingState.asSharedFlow()
    private var position = 0

    sealed class State {
        object Idle : State()
        data class ContentReady(val project: Project) : State()
    }

    fun loadCurrentProject(id: Long) {
        viewModelScope.launch {
            currentProject = getCurrentProjectUseCase(id)
            _state.update { State.ContentReady(currentProject) }
        }
    }

    fun startLoopingSubtitles() {
        viewModelScope.launch {
            while (isActive) {
                delay(0.5.seconds)
                _scrollingState.emit(position++)
            }
        }
    }

    fun stopLoopingSubtitles() {
        viewModelScope.launch {
            _scrollingState.emit(null)
        }
    }
}
