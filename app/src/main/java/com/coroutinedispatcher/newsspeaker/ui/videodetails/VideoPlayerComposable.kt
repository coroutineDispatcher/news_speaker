package com.coroutinedispatcher.newsspeaker.ui.videodetails

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer

@Composable
fun VideoPlayerComposable(modifier: Modifier = Modifier, videoUrl : String) {
    val context = LocalContext.current
    val screenHeightDp = LocalConfiguration.current.screenHeightDp.dp

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
                            Player.STATE_IDLE,
                            Player.STATE_BUFFERING,
                            Player.STATE_READY -> Unit
                            Player.STATE_ENDED -> onVideoItemEnd()
                        }
                    }
                })
            }
    }
}