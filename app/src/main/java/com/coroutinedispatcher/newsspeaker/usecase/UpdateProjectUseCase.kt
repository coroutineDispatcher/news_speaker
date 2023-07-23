package com.coroutinedispatcher.newsspeaker.usecase

import com.coroutinedispatcher.newsspeaker.database.Project
import com.coroutinedispatcher.newsspeaker.repository.ProjectsRepository
import javax.inject.Inject

class UpdateProjectUseCase @Inject constructor(
    private val repository: ProjectsRepository
) {
    suspend operator fun invoke(project: Project) = repository.updateProject(project)
}
