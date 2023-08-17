package com.coroutinedispatcher.newsspeaker.datasource

import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

class StorageDataSource @Inject constructor(
    @ApplicationContext private val context: Context
) {

    suspend fun delete(videoPath: String): Boolean = withContext(Dispatchers.IO) {
        val videoUri = Uri.parse(videoPath)

        // Resolve the actual file path from the content URI
        val projection = arrayOf(MediaStore.Video.Media.DATA)
        val cursor: Cursor? = context.contentResolver.query(videoUri, projection, null, null, null)

        cursor?.use {
            if (it.moveToFirst()) {
                val columnIndex = it.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)
                val videoFilePath = it.getString(columnIndex)

                // Create a File object and delete the file
                val videoFile = File(videoFilePath)
                if (videoFile.exists()) {
                    return@withContext videoFile.delete()
                }
            }
        }

        return@withContext false
    }

    suspend fun buildSharingIntent(videoUriString: String): Intent? =
        withContext(Dispatchers.IO) {
            val videoUri = Uri.parse(videoUriString)

            // Resolve the actual file path from the content URI
            val projection = arrayOf(MediaStore.Video.Media.DATA)
            val cursor: Cursor? =
                context.contentResolver.query(videoUri, projection, null, null, null)

            cursor?.use {
                if (it.moveToFirst()) {
                    val columnIndex = it.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)
                    val videoFilePath = it.getString(columnIndex)

                    val videoFile = File(videoFilePath)

                    val sharingIntent = Intent(Intent.ACTION_SEND)
                    sharingIntent.type = videoFile.getMimeType()
                    sharingIntent.putExtra(Intent.EXTRA_STREAM, videoUri)
                    sharingIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

                    return@withContext sharingIntent
                }
            }
            return@withContext null
        }

    private fun File.getMimeType(): String {
        val extension = this.extension
        return android.webkit.MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
            ?: "video/*"
    }
}
