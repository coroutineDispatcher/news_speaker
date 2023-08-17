package com.coroutinedispatcher.newsspeaker.ui.reusable

import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.coroutinedispatcher.newsspeaker.database.Project
import com.coroutinedispatcher.newsspeaker.theme.md_theme_dark_onPrimary
import com.coroutinedispatcher.newsspeaker.theme.md_theme_dark_outlineVariant
import com.coroutinedispatcher.newsspeaker.theme.md_theme_light_onPrimary
import com.coroutinedispatcher.newsspeaker.theme.md_theme_light_outlineVariant
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun ImageThumbnail(modifier: Modifier = Modifier, project: Project, onItemClicked: (Long) -> Unit) {
    Box(
        modifier = modifier
            .wrapContentSize()
            .clickable { onItemClicked(project.pId) }
    ) {
        val context = LocalContext.current

        if (project.videoPath.isEmpty()) {
            val emptyThumbnailColor = if (isSystemInDarkTheme()) {
                md_theme_dark_onPrimary
            } else {
                md_theme_light_onPrimary
            }
            val textColor = if (isSystemInDarkTheme()) {
                md_theme_light_outlineVariant
            } else {
                md_theme_dark_outlineVariant
            }
            Box(
                modifier = modifier
                    .width(300.dp)
                    .height(150.dp)
                    .align(Alignment.Center)
                    .background(color = emptyThumbnailColor)
            ) {
                Text(
                    text = project.title.split(" ").first(),
                    modifier = modifier
                        .align(Alignment.Center)
                        .padding(4.dp),
                    fontSize = 18.sp,
                    maxLines = 1,
                    color = textColor
                )
            }
        } else {
            val thumbnailBitmap = remember { mutableStateOf<Bitmap?>(null) }

            LaunchedEffect(key1 = Unit, block = {
                thumbnailBitmap.value = getVideoThumbnail(context, project.videoPath)
            })

            thumbnailBitmap.value?.let { thumbNail ->
                Image(
                    modifier = modifier.wrapContentSize(),
                    bitmap = thumbNail.asImageBitmap(),
                    contentDescription = ""
                )
            }
        }
    }
}

private suspend fun getVideoThumbnail(context: Context, path: String): Bitmap? {
    val videoUri = Uri.parse(path)
    val retriever = MediaMetadataRetriever()

    return withContext(Dispatchers.Default) {
        try {
            retriever.setDataSource(context, videoUri)
            retriever.frameAtTime?.let {
                Bitmap.createScaledBitmap(it, 1080, 1920, false)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        } finally {
            retriever.release()
        }
    }
}
