package com.edurda77.impuls.tele_tv.player

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.media3.common.C
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.ProgressiveMediaSource

@androidx.annotation.OptIn(UnstableApi::class)
@Composable
fun rememberPlayer(context: Context): ExoPlayer {
    val player = remember {
        ExoPlayer.Builder(context)
            //.setSeekForwardIncrementMs(10)
            //  .setSeekBackIncrementMs(10)
            .setMediaSourceFactory(
                ProgressiveMediaSource.Factory(DefaultDataSource.Factory(context))
            )
            .setVideoScalingMode(C.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING)
            .build()
            .apply {
                playWhenReady = true
                repeatMode = Player.REPEAT_MODE_OFF
            }
    }
    val lifecycleObserver = rememberMapLifecycleObserver(player)
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    DisposableEffect(lifecycle) {
        lifecycle.addObserver(lifecycleObserver)
        onDispose {
            lifecycle.removeObserver(lifecycleObserver)
        }
    }
    return  player
}

@Composable
fun rememberMapLifecycleObserver(player: ExoPlayer): LifecycleEventObserver =
    remember(player) {
        LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_STOP -> {
                    player.release()
                }

                Lifecycle.Event.ON_RESUME -> {
                    //player.stop()
                }

                Lifecycle.Event.ON_START -> {
                    player.play()
                }

                else -> {}
            }
        }
    }

