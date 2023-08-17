package com.coroutinedispatcher.newsspeaker.repository

import android.content.Intent
import com.coroutinedispatcher.newsspeaker.database.Project
import com.coroutinedispatcher.newsspeaker.datasource.ProjectsDataSource
import com.coroutinedispatcher.newsspeaker.datasource.StorageDataSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProjectsRepository @Inject constructor(
    private val projectsDataSource: ProjectsDataSource,
    private val storageDataSource: StorageDataSource
) {
    suspend fun createNewProject() = projectsDataSource.createNewProject()

    suspend fun updateProject(project: Project) = projectsDataSource.update(project)

    suspend fun getCurrentProject(projectId: Long) = projectsDataSource.getCurrentProjectById(
        projectId
    )

    fun getAllProjects(): Flow<List<Project>> = projectsDataSource.getAllProjects()
    suspend fun deleteCurrentProject(projectId: Long) {
        val currentProject = projectsDataSource.getCurrentProjectById(projectId)
        if (storageDataSource.delete(currentProject.videoPath)) {
            projectsDataSource.delete(currentProject)
        }
    }

    suspend fun shareProject(projectId: Long): Intent? {
        val currentProject = projectsDataSource.getCurrentProjectById(projectId)
        return storageDataSource.buildSharingIntent(currentProject.videoPath)
    }
}
