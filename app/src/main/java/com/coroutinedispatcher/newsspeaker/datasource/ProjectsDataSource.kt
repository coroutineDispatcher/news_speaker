package com.coroutinedispatcher.newsspeaker.datasource

import com.coroutinedispatcher.newsspeaker.database.Project
import com.coroutinedispatcher.newsspeaker.database.ProjectDao
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProjectsDataSource @Inject constructor(
    private val projectsDao: ProjectDao
) {

    fun createNewProject() = flow {
        emit(
            projectsDao.insert(
                Project(
                    pId = System.currentTimeMillis(),
                    title = "",
                    content = "",
                    videoPath = ""
                )
            )
        )
    }

    suspend fun update(which: Project) = projectsDao.update(
        which.copy(
            title = which.title,
            content = which.content,
            videoPath = which.videoPath
        )
    )

    fun delete(project: Project) = flow {
        projectsDao.delete(project)
        emit(Unit)
    }

    suspend fun getCurrentProjectById(id: Long) = projectsDao.getProject(id)
}