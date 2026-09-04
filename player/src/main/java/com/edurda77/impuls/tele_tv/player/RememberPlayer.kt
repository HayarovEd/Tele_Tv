package com.edurda77.impuls.tele_tv.player

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.media.session.MediaSession
import android.util.Log
import android.view.WindowManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.exoplayer.DefaultRenderersFactory
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.RenderersFactory
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import io.github.anilbeesetti.nextlib.media3ext.ffdecoder.NextRenderersFactory


@androidx.annotation.OptIn(UnstableApi::class)
@Composable
fun rememberPlayer(
    context: Context,
    setWakeLock: () -> Unit,
    releaseWakeLock: () -> Unit,
): ExoPlayer {
    val currentSetWakeLock = rememberUpdatedState(setWakeLock)
    val currentReleaseWakeLock = rememberUpdatedState(releaseWakeLock)

    val player = remember(context) {
        val decoder = DefaultRenderersFactory.EXTENSION_RENDERER_MODE_ON
        val renderersFactory: RenderersFactory =
            NextRenderersFactory(context).setEnableDecoderFallback(true)
                .setExtensionRendererMode(decoder)
        val audioAttributes =
            AudioAttributes.Builder()
                .setUsage(C.USAGE_MEDIA)
                .setContentType(C.AUDIO_CONTENT_TYPE_MOVIE)
                .build()
        val trackSelector = DefaultTrackSelector(context)
        ExoPlayer.Builder(context, renderersFactory)
            .setTrackSelector(trackSelector)
            .setAudioAttributes(audioAttributes,true)
            .setHandleAudioBecomingNoisy(true)
            .setMediaSourceFactory(
                ProgressiveMediaSource.Factory(DefaultDataSource.Factory(context))
            )
            .setVideoScalingMode(C.VIDEO_SCALING_MODE_SCALE_TO_FIT)
            .build()
            .apply {
                playWhenReady = true
                repeatMode = Player.REPEAT_MODE_OFF
            }
    }
    val lifecycleObserver = rememberMapLifecycleObserver(
        player = player,
        setWakeLock = { currentSetWakeLock.value() },
        releaseWakeLock = { currentReleaseWakeLock.value() }
    )
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    DisposableEffect(lifecycle, player) {
        lifecycle.addObserver(lifecycleObserver)
        onDispose {
            lifecycle.removeObserver(lifecycleObserver)
            currentReleaseWakeLock.value()
            player.release()
        }
    }
    return  player
}

@Composable
fun rememberMapLifecycleObserver(
    player: ExoPlayer,
    setWakeLock: () -> Unit,
    releaseWakeLock: () -> Unit,
): LifecycleEventObserver =
    remember(player) {
        LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_STOP -> {
                   // player.release()
                    Log.d(PlayerConstants.TAG, "on stop")
                }

                Lifecycle.Event.ON_CREATE -> {
                    Log.d(PlayerConstants.TAG, "on create")
                }

                Lifecycle.Event.ON_RESUME -> {
                    player.play()
                    setWakeLock()
                    Log.d(PlayerConstants.TAG, "on resume")
                }

                Lifecycle.Event.ON_START -> {
                    player.prepare()
                    Log.d(PlayerConstants.TAG, "on start")
                }

                Lifecycle.Event.ON_PAUSE -> {
                    player.stop()
                    releaseWakeLock()
                    Log.d(PlayerConstants.TAG, "on pause")
                }

                else -> {}
            }
        }
    }




