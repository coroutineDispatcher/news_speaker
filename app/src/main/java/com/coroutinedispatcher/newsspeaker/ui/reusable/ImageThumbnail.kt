package com.coroutinedispatcher.newsspeaker.ui.reusable

import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.coroutinedispatcher.newsspeaker.R
import com.coroutinedispatcher.newsspeaker.database.Project
import kotlin.math.roundToInt

@Composable
fun ImageThumbnail(modifier: Modifier = Modifier, project: Project, onItemClicked: (Long) -> Unit) {
    Box(
        modifier = modifier
            .wrapContentSize()
            .clickable { onItemClicked(project.pId) }
    ) {
        val context = LocalContext.current

        if (project.videoPath.isEmpty()) {
            Box(
                modifier = modifier
                    .width(
                        with(LocalDensity.current) {
                            pxToDp(1080 / 2, this.density)
                        }
                    )
                    .height(
                        with(LocalDensity.current) {
                            pxToDp(
                                1920 / 2,
                                this.density
                            )
                        }
                    )
                    .background(color = Color.LightGray)
            ) {
                Image(
                    modifier = modifier.align(Alignment.Center),
                    painter = painterResource(R.drawable.baseline_camera_24),
                    contentDescription = ""
                )
            }
        } else {
            val thumbnailBitmap = getVideoThumbnail(context, project.videoPath)

            Image(
                modifier = modifier.wrapContentSize(),
                bitmap = checkNotNull(thumbnailBitmap).asImageBitmap(),
                contentDescription = ""
            )
        }
        Text(
            text = project.title,
            modifier = modifier.align(Alignment.BottomCenter),
            color = Color.White,
            fontSize = 32.sp,
            maxLines = 1
        )
    }
}

private fun getVideoThumbnail(context: Context, path: String): Bitmap? {
    val videoUri = Uri.parse(path)

    val retriever = MediaMetadataRetriever()
    try {
        retriever.setDataSource(context, videoUri)
        return retriever.frameAtTime?.let {
            Bitmap.createScaledBitmap(it, 1080, 1920, false)
        }
    } catch (e: Exception) {
        e.printStackTrace()
    } finally {
        retriever.release()
    }
    return null
}

fun pxToDp(px: Int, density: Float): Dp {
    return (px / density).roundToInt().dp
}
