package com.coroutinedispatcher.newsspeaker.ui.camera

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.coroutinedispatcher.newsspeaker.database.Project
import com.coroutinedispatcher.newsspeaker.usecase.GetCurrentProjectUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CameraViewModel @Inject constructor(
    private val getCurrentProjectUseCase: GetCurrentProjectUseCase
) : ViewModel() {
    private lateinit var currentProject: Project

    fun getCurrentProject(id: Long) {
        viewModelScope.launch { currentProject = getCurrentProjectUseCase(id) }
    }
}
