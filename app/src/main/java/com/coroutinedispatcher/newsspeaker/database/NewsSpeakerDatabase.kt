package com.coroutinedispatcher.newsspeaker.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Project::class], version = 1, exportSchema = false)
abstract class NewsSpeakerDatabase : RoomDatabase() {
    abstract fun projectsDao(): ProjectDao
}
