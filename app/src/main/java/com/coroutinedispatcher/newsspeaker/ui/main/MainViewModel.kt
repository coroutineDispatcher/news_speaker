package com.coroutinedispatcher.newsspeaker.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.coroutinedispatcher.newsspeaker.database.Project
import com.coroutinedispatcher.newsspeaker.usecase.GetAllProjectsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getAllProjectsUseCase: GetAllProjectsUseCase
) : ViewModel() {

    sealed class State {
        object Empty : State()
        data class Success(val data: List<Project>) : State()
    }

    val state = getAllProjectsUseCase()
        .map { projects ->
            if (projects.isEmpty()) {
                State.Empty
            } else {
                State.Success(projects)
            }
        }.stateIn(viewModelScope, SharingStarted.Lazily, State.Empty)
}
