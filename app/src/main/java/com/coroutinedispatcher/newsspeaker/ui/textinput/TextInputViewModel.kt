package com.coroutinedispatcher.newsspeaker.ui.textinput

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.coroutinedispatcher.newsspeaker.database.Project
import com.coroutinedispatcher.newsspeaker.usecase.CreateNewProjectUseCase
import com.coroutinedispatcher.newsspeaker.usecase.DeleteProjectUseCase
import com.coroutinedispatcher.newsspeaker.usecase.GetCurrentProjectUseCase
import com.coroutinedispatcher.newsspeaker.usecase.UpdateProjectUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

// TODO: Rework. It's not well thought
@HiltViewModel
class TextInputViewModel @Inject constructor(
    private val createNewProjectUseCase: CreateNewProjectUseCase,
    private val updateProjectUseCase: UpdateProjectUseCase,
    private val getCurrentProjectUseCase: GetCurrentProjectUseCase,
    private val deleteProjectUseCase: DeleteProjectUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<State>(State())
    val state: StateFlow<State> = _state.asStateFlow()
    val titleText = mutableStateOf("")
    val contentText = mutableStateOf("")

    data class State(val project: Project? = null, val finish: Boolean = false)

    fun getOrCreate(projectId: Long = -1) {
        viewModelScope.launch(Dispatchers.IO) {
            if (projectId == -1L) {
                createNewProjectUseCase().collectLatest { projectId ->
                    updateStateWithCurrentProject(projectId)
                }
            } else {
                updateStateWithCurrentProject(projectId)
            }
        }
    }

    fun update(content: String, title: String) {
        val currentState = checkNotNull(_state.value.project)
        viewModelScope.launch {
            updateProjectUseCase(currentState.copy(content = content, title = title))
        }
    }

    private suspend fun updateStateWithCurrentProject(id: Long) {
        val project = getCurrentProjectUseCase(id)
        titleText.value = project.title
        contentText.value = project.content
        _state.update { it.copy(project = project) }
    }
}
