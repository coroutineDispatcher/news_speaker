package com.coroutinedispatcher.newsspeaker.datasource

import com.coroutinedispatcher.newsspeaker.database.Project
import com.coroutinedispatcher.newsspeaker.database.ProjectDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProjectsDataSource @Inject constructor(
    private val projectsDao: ProjectDao
) {

    suspend fun createNewProject() = projectsDao.insert(
        Project(
            pId = System.currentTimeMillis(),
            title = "",
            content = "",
            videoPath = ""
        )
    )

    suspend fun update(project: Project) = projectsDao.update(
        project.copy(
            title = project.title,
            content = project.content,
            videoPath = project.videoPath
        )
    )

    suspend fun delete(project: Project) = withContext(Dispatchers.IO) {
        projectsDao.delete(project)
    }

    suspend fun getCurrentProjectById(id: Long) = projectsDao.getProject(id)
    fun getAllProjects(): Flow<List<Project>> = projectsDao.getAll()
}
