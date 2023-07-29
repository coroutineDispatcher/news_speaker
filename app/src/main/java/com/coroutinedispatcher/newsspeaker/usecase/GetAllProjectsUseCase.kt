package com.coroutinedispatcher.newsspeaker.usecase

import com.coroutinedispatcher.newsspeaker.repository.ProjectsRepository
import javax.inject.Inject

class GetAllProjectsUseCase @Inject constructor(
    private val projectsRepository: ProjectsRepository
) {
    operator fun invoke() = projectsRepository.getAllProjects()
}