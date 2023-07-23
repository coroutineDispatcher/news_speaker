package com.coroutinedispatcher.newsspeaker.di

import android.content.Context
import androidx.room.Room
import com.coroutinedispatcher.newsspeaker.database.NewsSpeakerDatabase
import com.coroutinedispatcher.newsspeaker.database.ProjectDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideNewSpeakerDatabase(
        @ApplicationContext applicationContext: Context
    ): NewsSpeakerDatabase = Room.databaseBuilder(
        applicationContext,
        NewsSpeakerDatabase::class.java,
        "news_speaker_database"
    )
        .fallbackToDestructiveMigration()
        .build()

    @Provides
    @Singleton
    fun provideProjectsDao(newsSpeakerDatabase: NewsSpeakerDatabase): ProjectDao =
        newsSpeakerDatabase.projectsDao()
}
