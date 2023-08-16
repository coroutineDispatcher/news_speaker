package com.coroutinedispatcher.newsspeaker.database

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity
data class Project(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val pId: Long,
    @ColumnInfo(name = "title")
    val title: String,
    @ColumnInfo(name = "content")
    val content: String,
    @ColumnInfo(name = "path")
    val videoPath: String
) : Parcelable
