package com.coroutinedispatcher.newsspeaker.usecase

import com.coroutinedispatcher.newsspeaker.repository.ProjectsRepository
import javax.inject.Inject

class GetCurrentProjectUseCase @Inject constructor(
    private val repository: ProjectsRepository
) {
    suspend operator fun invoke(projectId: Long) = repository.getCurrentProject(projectId)
}