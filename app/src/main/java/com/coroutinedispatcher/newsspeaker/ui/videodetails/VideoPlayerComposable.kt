package com.coroutinedispatcher.newsspeaker.ui.videodetails

import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView

@Composable
fun VideoPlayerComposable(
    modifier: Modifier = Modifier,
    videoUrl: String,
    onVideoReady: () -> Unit,
    onVideoEnded: () -> Unit,
    onVideoIdle: () -> Unit,
    onVideoLoading: () -> Unit,
    onDeleteButtonClicked: () -> Unit,
    onShareButtonClicked: () -> Unit
) {
    val context = LocalContext.current

    val exoplayer = remember {
        ExoPlayer.Builder(context).build()
            .apply {
                setMediaItem(
                    MediaItem.fromUri(videoUrl)
                )
                prepare()
                playWhenReady = true

                addListener(object : Player.Listener {
                    override fun onPlaybackStateChanged(playbackState: Int) {
                        when (playbackState) {
                            Player.STATE_IDLE -> onVideoIdle()
                            Player.STATE_BUFFERING -> onVideoLoading()
                            Player.STATE_READY -> onVideoReady()
                            Player.STATE_ENDED -> onVideoEnded()
                        }
                    }
                })
            }
    }

    Row(
        modifier = Modifier
            .fillMaxSize()
    ) {
        DisposableEffect(key1 = Unit) {
            onDispose {
                with(exoplayer) {
                    stop()
                    release()
                }
            }
        }

        Box(
            modifier = modifier
                .fillMaxSize()
                .background(color = Color.Black)
        ) {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = {
                    PlayerView(context).apply {
                        player = exoplayer
                        layoutParams = FrameLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )

                        with(exoplayer) {
                            seekTo(0)
                            play()
                        }
                    }
                }
            )
            Row(
                modifier = modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.End
            ) {
                val deleteIcon = Icons.Default.Delete
                Icon(
                    deleteIcon,
                    contentDescription = deleteIcon.name,
                    modifier = modifier
                        .padding(
                            top = 32.dp,
                            end = 32.dp
                        )
                        .clickable {
                            exoplayer.stop()
                            onDeleteButtonClicked()
                        }
                )
                val shareIcon = Icons.Default.Share
                Icon(
                    shareIcon,
                    contentDescription = deleteIcon.name,
                    modifier = modifier
                        .padding(
                            top = 32.dp,
                            end = 32.dp
                        )
                        .clickable {
                            exoplayer.stop()
                            onShareButtonClicked()
                        }
                )
            }
        }
    }
}
