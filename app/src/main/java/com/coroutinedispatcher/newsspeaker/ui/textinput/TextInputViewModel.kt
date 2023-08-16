package com.coroutinedispatcher.newsspeaker.ui.textinput

import androidx.lifecycle.ViewModel
import com.coroutinedispatcher.newsspeaker.database.Project
import com.coroutinedispatcher.newsspeaker.usecase.CreateNewProjectUseCase
import com.coroutinedispatcher.newsspeaker.usecase.DeleteProjectUseCase
import com.coroutinedispatcher.newsspeaker.usecase.GetCurrentProjectUseCase
import com.coroutinedispatcher.newsspeaker.usecase.UpdateProjectUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class TextInputViewModel @Inject constructor(
    private val createNewProjectUseCase: CreateNewProjectUseCase,
    private val updateProjectUseCase: UpdateProjectUseCase,
    private val getCurrentProjectUseCase: GetCurrentProjectUseCase,
    private val deleteProjectUseCase: DeleteProjectUseCase
) : ViewModel() {
    private val _state = MutableStateFlow<State>(State())
    val state: StateFlow<State> = _state.asStateFlow()

    data class State(val project: Project? = null, val finish: Boolean = false)
}
