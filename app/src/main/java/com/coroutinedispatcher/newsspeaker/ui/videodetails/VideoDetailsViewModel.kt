package com.coroutinedispatcher.newsspeaker.ui.videodetails

import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.coroutinedispatcher.newsspeaker.database.Project
import com.coroutinedispatcher.newsspeaker.usecase.DeleteProjectUseCase
import com.coroutinedispatcher.newsspeaker.usecase.GetCurrentProjectUseCase
import com.coroutinedispatcher.newsspeaker.usecase.ShareProjectUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VideoDetailsViewModel @Inject constructor(
    private val getCurrentProjectUseCase: GetCurrentProjectUseCase,
    private val deleteProjectUseCase: DeleteProjectUseCase,
    private val shareProjectUseCase: ShareProjectUseCase
) : ViewModel() {
    private val _state = MutableStateFlow<State>(State.Loading)
    val state = _state.asStateFlow()

    sealed class State {
        object Loading : State()
        data class Success(val project: Project) : State()
        object ProjectDeleted : State()
        data class SharingReady(val intent: Intent) : State()
    }

    fun loadProject(pId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val project = getCurrentProjectUseCase(pId)
            _state.update {
                State.Success(project)
            }
        }
    }

    fun deleteProject(id: Long) {
        viewModelScope.launch {
            _state.update { State.Loading }
            deleteProjectUseCase(id)
            _state.update { State.ProjectDeleted }
        }
    }

    fun shareProject(projectId: Long) {
        viewModelScope.launch {
            val sharingIntent = shareProjectUseCase(projectId)
            sharingIntent?.let { intent ->
                _state.update { State.SharingReady(intent) }
            }
        }
    }
}
