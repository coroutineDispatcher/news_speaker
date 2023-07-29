package com.coroutinedispatcher.newsspeaker.ui.camera

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.coroutinedispatcher.newsspeaker.database.Project
import com.coroutinedispatcher.newsspeaker.usecase.GetCurrentProjectUseCase
import com.coroutinedispatcher.newsspeaker.usecase.UpdateProjectUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CameraViewModel @Inject constructor(
    private val getCurrentProjectUseCase: GetCurrentProjectUseCase,
    private val updateCurrentProjectUseCase: UpdateProjectUseCase
) : ViewModel() {
    private lateinit var currentProject: Project
    private val _state = MutableStateFlow<State>(State.Idle)
    val state = _state.asStateFlow()

    sealed class State {
        object Idle : State()
        object Finished : State()
    }

    fun loadCurrentProject(id: Long) {
        viewModelScope.launch { currentProject = getCurrentProjectUseCase(id) }
    }

    fun saveProjectToDatabase(outputUri: Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            updateCurrentProjectUseCase(currentProject.copy(videoPath = outputUri.toString()))
            _state.update { State.Finished }
        }
    }
}
