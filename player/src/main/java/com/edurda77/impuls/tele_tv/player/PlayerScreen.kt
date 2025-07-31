package com.edurda77.impuls.tele_tv.player

import android.util.Log
import android.view.KeyEvent
import androidx.annotation.OptIn
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.source.MediaSource
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.media3.ui.compose.PlayerSurface
import androidx.media3.ui.compose.SURFACE_TYPE_TEXTURE_VIEW
import androidx.media3.ui.compose.modifiers.resizeWithContentScale
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.edurda77.impuls.tele_tv.domain.model.Credintial
import com.edurda77.impuls.tele_tv.resources.R
import com.edurda77.impuls.tele_tv.resources.theme.Tele_TvTheme
import kotlinx.coroutines.delay
import okhttp3.Credentials
import org.koin.androidx.compose.koinViewModel
import java.util.Collections

@Composable
fun PlayerScreenRoot(
    viewModel: PlayerScreenViewModel = koinViewModel(),
    isTv: Boolean,
    onNavigateToChannels: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    PlayerScreenScreen(
        state = state,
        onAction = viewModel::onAction,
        isTv = isTv,
        onNavigateToChannels = onNavigateToChannels
    )
}

@OptIn(UnstableApi::class)
@Composable
fun PlayerScreenScreen(
    modifier: Modifier = Modifier,
    state: PlayerScreenState,
    isTv: Boolean,
    onAction: (PlayerScreenAction) -> Unit,
    onNavigateToChannels: () -> Unit
) {
    val context = LocalContext.current
    val exoPlayer = rememberPlayer(context)
    var isLoadingChannel by remember { mutableStateOf(true) }
    val playerListener = object : Player.Listener {

        override fun onPlayerError(error: PlaybackException) {
            super.onPlayerError(error)
            val errorCode = error.errorCode
            Log.d("REST TELE TV", "errorCode play $errorCode")
            Log.d("REST TELE TV", "error play $error")
        }


        override fun onIsPlayingChanged(isPlaying: Boolean) {
            super.onIsPlayingChanged(isPlaying)
            Log.d("REST TELE TV", "isPlaying $isPlaying")
            isLoadingChannel = !isPlaying
        }
    }


    exoPlayer.addListener(playerListener)
    val focusRequester = remember { FocusRequester() }
    val interactionSource = remember { MutableInteractionSource() }
    val focusManager = LocalFocusManager.current
    val configuration = LocalWindowInfo.current.containerSize
    val screenHeight = configuration.height.dp
    val screenWidth = configuration.width.dp
    var isEpgVisible by remember { mutableStateOf(false) }


    LaunchedEffect(exoPlayer, state.tvChannels, state.selectedChannelId) {

        if (state.tvChannels.isNotEmpty()) {
            state.credintial?.let {
                state.selectedIndex?.let {
                    exoPlayer.setMediaSource(
                        intoMediaItem(
                            credintial = state.credintial,
                            uri = state.tvChannels[state.selectedIndex].url
                        )
                    )
                    exoPlayer.prepare()
                    exoPlayer.playWhenReady = true
                }
            }
        }
    }

    LaunchedEffect(state.volume) {
        exoPlayer.volume = state.volume
    }

    LaunchedEffect(state.isVisibleSideMenu) {
        if (state.isVisibleSideMenu) {
            delay(300)
            focusManager.moveFocus(FocusDirection.Left)
            focusRequester.requestFocus()
        }
    }
    Box {
        PlayerSurface(
            modifier = modifier
                .resizeWithContentScale(
                    contentScale = ContentScale.Fit,
                    sourceSizeDp = null
                )
                .focusRequester(focusRequester)
                .focusable(interactionSource = interactionSource)
                .onKeyEvent {
                    if (it.nativeKeyEvent.action == KeyEvent.ACTION_UP) {
                        Log.d("REST TELE TV", "action ${it.nativeKeyEvent.keyCode}")
                        when (it.nativeKeyEvent.keyCode) {
                            KeyEvent.KEYCODE_DPAD_UP, KeyEvent.KEYCODE_PAGE_UP -> {
                                onAction(PlayerScreenAction.IncrimentTvChannel)
                            }

                            KeyEvent.KEYCODE_DPAD_DOWN, KeyEvent.KEYCODE_PAGE_DOWN -> {
                                onAction(PlayerScreenAction.DecrimentTvChannel)
                            }

                            KeyEvent.KEYCODE_DPAD_LEFT, KeyEvent.KEYCODE_ENTER -> {
                                onAction(PlayerScreenAction.ShowSideMenu)
                                focusManager.moveFocus(FocusDirection.Left)
                            }

                            KeyEvent.KEYCODE_BACK -> {
                                onNavigateToChannels()
                            }

                            KeyEvent.KEYCODE_0,
                            KeyEvent.KEYCODE_1,
                            KeyEvent.KEYCODE_2,
                            KeyEvent.KEYCODE_3,
                            KeyEvent.KEYCODE_4,
                            KeyEvent.KEYCODE_5,
                            KeyEvent.KEYCODE_6,
                            KeyEvent.KEYCODE_7,
                            KeyEvent.KEYCODE_8,
                            KeyEvent.KEYCODE_9,
                                -> {
                                onAction(PlayerScreenAction.EnterStringNumber(it.nativeKeyEvent.keyCode - 7))
                            }

                            KeyEvent.KEYCODE_DEL -> {
                                onAction(PlayerScreenAction.DeleteLastNumber)
                            }

                            KeyEvent.KEYCODE_VOLUME_UP -> {
                                if (!isTv) {
                                    onAction(PlayerScreenAction.IncrimentVolume)
                                }
                            }

                            KeyEvent.KEYCODE_VOLUME_DOWN -> {
                                if (!isTv) {
                                    onAction(PlayerScreenAction.DecrimentVolume)
                                }
                            }
                        }
                    }
                    true
                },
            player = exoPlayer,
            surfaceType = SURFACE_TYPE_TEXTURE_VIEW
        )
        if (isLoadingChannel) {
            TvCustomCircularProgressIndicator(
                modifier = modifier
                    .align (Alignment.Center)
                    .size(screenHeight/7)
            )
        }
        if (state.isVisibleSideMenu || isEpgVisible) {
            Row(
                modifier = modifier
                    .width(if (isEpgVisible) screenHeight else screenHeight / 4)
                    .background(MaterialTheme.colorScheme.background.copy(alpha = if (isEpgVisible) 0.9f else 0.5f))
            ) {
                ChannelMenu(
                    modifier = modifier
                        .weight(1f)
                        .focusRequester(focusRequester)
                        .focusable(interactionSource = interactionSource)
                        .onKeyEvent {
                            if (it.nativeKeyEvent.action == KeyEvent.ACTION_UP) {
                                when (it.nativeKeyEvent.keyCode) {
                                    KeyEvent.KEYCODE_DPAD_RIGHT -> {
                                        if (isEpgVisible) {
                                            focusManager.moveFocus(FocusDirection.Right)
                                        } else {
                                            onAction(PlayerScreenAction.GetEpgByFocusedChannelId)
                                            isEpgVisible = true
                                        }
                                    }

                                    KeyEvent.KEYCODE_BACK -> {
                                        isEpgVisible = false
                                        onAction(PlayerScreenAction.OnResetMenuTimer)
                                    }

                                    KeyEvent.KEYCODE_DPAD_DOWN -> {
                                        onAction(PlayerScreenAction.IncrimentFocusedIndex)
                                    }

                                    KeyEvent.KEYCODE_DPAD_UP -> {
                                        onAction(PlayerScreenAction.DecrimentFocusedIndex)
                                    }

                                    KeyEvent.KEYCODE_DPAD_CENTER -> {
                                        onAction(PlayerScreenAction.UpdateSelectedIndex)
                                    }
                                    KeyEvent.KEYCODE_DPAD_LEFT -> {
                                        if (isEpgVisible) {
                                            onAction(PlayerScreenAction.OnRestartMenuTimer(10))
                                        } else {
                                            onAction(PlayerScreenAction.OnResetMenuTimer)
                                        }
                                        isEpgVisible = false
                                    }
                                }
                            }
                            true
                        },
                    channels = state.tvChannels,
                    selectedIndex = state.selectedIndex,
                    focusedIndex = state.focusedIndex,
                    allTvEpg = state.allTvEpg,
                    currentTime = state.currentTime,
                    heightItem = screenWidth/30,
                )
                if (isEpgVisible) {
                    if (state.isLoadingFocusedChannelEpg) {
                        Box(modifier = modifier.weight(3f)) {
                            Text(
                                modifier = modifier
                                    .align(Alignment.Center),
                                text = stringResource(R.string.epg_udpating),
                                style = MaterialTheme.typography.headlineMedium,
                                color = MaterialTheme.colorScheme.tertiary,
                                textAlign = TextAlign.Center
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = modifier
                                .weight(3f)
                                .onKeyEvent {
                                    if (it.nativeKeyEvent.action == KeyEvent.ACTION_UP) {
                                        when (it.nativeKeyEvent.keyCode) {
                                            KeyEvent.KEYCODE_BACK -> {
                                                isEpgVisible = false
                                                onAction(PlayerScreenAction.OnResetMenuTimer)
                                            }

                                            KeyEvent.KEYCODE_DPAD_DOWN -> {
                                                focusManager.moveFocus(FocusDirection.Down)
                                            }

                                            KeyEvent.KEYCODE_DPAD_UP -> {
                                                focusManager.moveFocus(FocusDirection.Up)
                                            }

                                            KeyEvent.KEYCODE_DPAD_LEFT -> {
                                                focusManager.moveFocus(FocusDirection.Left)
                                                onAction(PlayerScreenAction.OnRestartMenuTimer(10))
                                            }
                                        }
                                    }
                                    true
                                },
                            contentPadding = PaddingValues(horizontal = 35.dp, vertical = 15.dp),
                            verticalArrangement = Arrangement.spacedBy(15.dp)
                        ) {
                            state.focusedChannelEpg.forEach { entry ->
                                stickyHeader {
                                    Text(
                                        modifier = modifier
                                            .fillMaxWidth()
                                            .background(MaterialTheme.colorScheme.secondary),
                                        text = entry.key.toString(),
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.onSecondary,
                                        textAlign = TextAlign.Center
                                    )
                                }
                                items(items = entry.value,
                                    key = {
                                        it.eventId
                                    }) {
                                    ItemFullTvEpg(
                                        tvEpg = it,
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
        if (state.channelInputQuery.isNotEmpty()) {
            Text(
                modifier = modifier
                    .align(Alignment.TopCenter)
                    .padding(vertical = 15.dp)
                    .background(MaterialTheme.colorScheme.secondaryContainer)
                    .padding(15.dp),
                text = state.channelInputQuery,
                fontSize = 30.sp,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.errorContainer,
            )
        }
        AnimatedVisibility(
            visible = state.isVisibleVolumeProgress,
            modifier = modifier
                .align(Alignment.CenterEnd)
                .padding(end = screenWidth / 30),
            enter = slideInHorizontally { it },
            exit = slideOutHorizontally { it }
        ) {
            TvVolumeProgress(
                modifier = modifier.align(Alignment.CenterEnd),
                progressHeight = screenHeight / 3,
                volume = state.volume
            )
        }
    }
}

@Preview
@Composable
private fun Preview() {
    Tele_TvTheme {
        PlayerScreenScreen(
            state = PlayerScreenState(),
            isTv = false,
            onAction = {},
            onNavigateToChannels = {}
        )
    }
}

@OptIn(UnstableApi::class)
private fun intoMediaItem(
    credintial: Credintial,
    uri: String
): MediaSource {
    Log.d("TEST TELE TV", "uri $uri")
    val credentials = Credentials.basic(credintial.username, credintial.password)
    val dataSourceFactory = DefaultHttpDataSource.Factory()
        .setDefaultRequestProperties(
            Collections.singletonMap("Authorization", credentials)
        )
        .setConnectTimeoutMs(DefaultHttpDataSource.DEFAULT_CONNECT_TIMEOUT_MILLIS)
        .setReadTimeoutMs(DefaultHttpDataSource.DEFAULT_READ_TIMEOUT_MILLIS)

    /* val mediaSource =
         DashMediaSource.Factory(dataSourceFactory).createMediaSource(MediaItem.fromUri(uri))*/

    val mediaSource = ProgressiveMediaSource.Factory(dataSourceFactory)
        .createMediaSource(MediaItem.fromUri(uri))

    return mediaSource
}
