package com.coroutinedispatcher.newsspeaker.repository

import com.coroutinedispatcher.newsspeaker.database.Project
import com.coroutinedispatcher.newsspeaker.datasource.ProjectsDataSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProjectsRepository @Inject constructor(
    private val projectsDataSource: ProjectsDataSource
) {
    fun createNewProject() = projectsDataSource.createNewProject()

    suspend fun updateProject(project: Project) = projectsDataSource.update(project)

    suspend fun getCurrentProject(projectId: Long) = projectsDataSource.getCurrentProjectById(
        projectId
    )

    fun getAllProjects(): Flow<List<Project>> = projectsDataSource.getAllProjects()
}
