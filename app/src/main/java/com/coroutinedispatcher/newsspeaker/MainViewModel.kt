package com.coroutinedispatcher.newsspeaker

import android.net.Uri
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.coroutinedispatcher.newsspeaker.database.Project
import com.coroutinedispatcher.newsspeaker.usecase.CreateNewProjectUseCase
import com.coroutinedispatcher.newsspeaker.usecase.GetCurrentProjectUseCase
import com.coroutinedispatcher.newsspeaker.usecase.UpdateProjectUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val createNewProjectUseCase: CreateNewProjectUseCase,
    private val updateProjectUseCase: UpdateProjectUseCase,
    private val getCurrentProjectUseCase: GetCurrentProjectUseCase,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    val projectState = savedStateHandle.getStateFlow(CurrentProjectKey, null)
    private val _navigateToCamera = MutableSharedFlow<Boolean>()
    val navigateToCamera = _navigateToCamera.asSharedFlow()
    val currentProject: Project?
        get() = (savedStateHandle.get(CurrentProjectKey) as? Project)

    fun createNewProject() {
        viewModelScope.launch(Dispatchers.IO) {
            val newProject = createNewProjectUseCase()
            savedStateHandle[CurrentProjectKey] = getCurrentProjectUseCase(newProject)
            Log.d(TAG, "createNewProject: $newProject")
        }
    }

    fun updateTitle(title: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val project =
                checkNotNull(currentProject) { "At this stage the project cannot be null" }
            val updatedProject = project.copy(title = title)
            updateProjectUseCase(updatedProject)
            savedStateHandle[CurrentProjectKey] = updatedProject
            Log.d(TAG, "updateTitle: $updatedProject")
        }
    }

    fun updateContent(content: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val project =
                checkNotNull(currentProject) { "At this stage the project cannot be null" }
            val updatedProject = project.copy(content = content)
            updateProjectUseCase(updatedProject)
            savedStateHandle[CurrentProjectKey] = updatedProject
            Log.d(TAG, "updateContent: $updatedProject")
        }
    }

    fun updatePath(path: Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            val project =
                checkNotNull(currentProject) { "At this stage the project cannot be null" }
            val updatedProject = project.copy(videoPath = path.toString())
            updateProjectUseCase(updatedProject)
            savedStateHandle[CurrentProjectKey] = updatedProject
            Log.d(TAG, "updatePath: $updatedProject")
        }
    }

    fun updateProject(project: Project) {
        viewModelScope.launch(Dispatchers.IO) {
            updateProjectUseCase(project)
            savedStateHandle[CurrentProjectKey] = project
        }
    }

    fun updateTitleAndContent(title: String, content: String) {
        viewModelScope.launch {
            val project =
                checkNotNull(currentProject) { "At this stage the project cannot be null" }
            val updatedProject = project.copy(content = content, title = title)
            updateProjectUseCase(updatedProject)
            savedStateHandle[CurrentProjectKey] = updatedProject
            Log.d(TAG, "updateContent: $updatedProject")
            _navigateToCamera.emit(true)
        }
    }

    companion object {
        private const val CurrentProjectKey = "current_project_key"
        private val TAG = MainViewModel::class.java.simpleName
    }
}