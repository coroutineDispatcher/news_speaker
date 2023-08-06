package com.coroutinedispatcher.newsspeaker.usecase

import android.content.Intent
import com.coroutinedispatcher.newsspeaker.repository.ProjectsRepository
import javax.inject.Inject

class ShareProjectUseCase @Inject constructor(
    private val projectsRepository: ProjectsRepository
) {
    suspend operator fun invoke(projectId: Long): Intent? =
        projectsRepository.shareProject(projectId)
}
