package com.edurda77.impuls.tele_tv.player

import android.util.Log
import android.view.KeyEvent
import androidx.annotation.OptIn
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.media3.ui.compose.PlayerSurface
import androidx.media3.ui.compose.SURFACE_TYPE_TEXTURE_VIEW
import androidx.media3.ui.compose.modifiers.resizeWithContentScale
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.edurda77.impuls.tele_tv.domain.model.Credintial
import com.edurda77.impuls.tele_tv.resources.theme.Tele_TvTheme
import kotlinx.coroutines.delay
import okhttp3.Credentials
import org.koin.androidx.compose.koinViewModel
import java.util.Collections

@Composable
fun PlayerScreenRoot(
    viewModel: PlayerScreenViewModel = koinViewModel(),
    onNavigateToChannels: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    PlayerScreenScreen(
        state = state,
        onAction = viewModel::onAction,
        onNavigateToChannels = onNavigateToChannels
    )
}

@OptIn(UnstableApi::class)
@Composable
fun PlayerScreenScreen(
    modifier: Modifier = Modifier,
    state: PlayerScreenState,
    onAction: (PlayerScreenAction) -> Unit,
    onNavigateToChannels: () -> Unit
) {
    val context = LocalContext.current
    val exoPlayer = rememberPlayer(context)
    val playerListener = object : Player.Listener {
        override fun onPlayerError(error: PlaybackException) {
            super.onPlayerError(error)
            val errorCode = error.errorCode
            Log.d("REST TELE TV", "errorCode play $errorCode")
            Log.d("REST TELE TV", "error play $error")
        }
    }
    exoPlayer.addListener(playerListener)
    val focusRequester = remember { FocusRequester() }
    val interactionSource = remember { MutableInteractionSource() }
    val focusManager = LocalFocusManager.current
    //var isMenuVisible by remember { mutableStateOf(false) }

    LaunchedEffect(exoPlayer, state.tvChannels, state.selectedIndex) {
        if (state.tvChannels.isNotEmpty()) {
            state.credintial?.let {
                exoPlayer.setMediaSource(
                    intoMediaItem(
                        credintial = state.credintial,
                        uri = state.tvChannels[state.selectedIndex].url
                    )
                )
                exoPlayer.prepare()
            }
        }
    }

    LaunchedEffect(state.isVisibleSideMenu) {
        if (state.isVisibleSideMenu) {
            delay(300)
            focusManager.moveFocus(FocusDirection.Left)
            focusRequester.requestFocus()
        }
    }
    Box() {
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
                            KeyEvent.KEYCODE_DPAD_DOWN, KeyEvent.KEYCODE_PAGE_UP -> {
                                onAction(PlayerScreenAction.IncrimentTvChannel)
                            }

                            KeyEvent.KEYCODE_DPAD_UP, KeyEvent.KEYCODE_PAGE_DOWN -> {
                                onAction(PlayerScreenAction.DecrimentTvChannel)
                            }

                            KeyEvent.KEYCODE_DPAD_RIGHT -> {
                                onAction(PlayerScreenAction.OnShowTitle)
                            }

                            KeyEvent.KEYCODE_DPAD_LEFT, KeyEvent.KEYCODE_ENTER -> {
                               // isMenuVisible = true
                                onAction(PlayerScreenAction.ShowSideMenu)
                                focusManager.moveFocus(FocusDirection.Left)
                                // onAction(PlayerScreenAction.ShowSideMenu)
                                // onChangeFocus(FocusDirection.Exit)
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
                            KeyEvent.KEYCODE_9,     -> {
                                onAction(PlayerScreenAction.EnterStringNumber(it.nativeKeyEvent.keyCode-7))
                            }
                        }
                    }
                    true
                },
            player = exoPlayer,
            surfaceType = SURFACE_TYPE_TEXTURE_VIEW,
        )
        AnimatedVisibility(
            visible = state.isVisibleTitle,
            modifier = modifier.align(Alignment.BottomCenter),
            enter = slideInVertically { it },
            exit = slideOutVertically { it }
        ) {
            DrumMenu(
                channels = state.tvChannels,
                selectedIndex = state.selectedIndex
            )
        }
        AnimatedVisibility(
            visible = state.isVisibleSideMenu,
            modifier = modifier.align(Alignment.CenterStart),
            enter = slideInHorizontally { 0 },
            exit = slideOutHorizontally { -it }
        ) {
            ChannelMenu(
                modifier = modifier
                    .focusRequester(focusRequester)
                    .focusable(interactionSource = interactionSource)
                    /*.onFocusChanged {
                        Log.d("TELE TV TEST", "isFocused ${it.isFocused}")
                    }*/
                    .onKeyEvent {
                        if (it.nativeKeyEvent.action == KeyEvent.ACTION_UP) {
                            when (it.nativeKeyEvent.keyCode) {
                                KeyEvent.KEYCODE_DPAD_RIGHT, KeyEvent.KEYCODE_BACK -> {
                                    //isMenuVisible = false
                                    focusManager.moveFocus(FocusDirection.Right)
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
                            }
                        }
                        true
                    },
                channels = state.tvChannels,
                selectedIndex = state.selectedIndex,
                focusedIndex = state.focusedIndex
            )
        }
        if (state.channelInputQuery.isNotEmpty()) {
            Text(
                modifier = modifier
                    .align(Alignment.TopCenter),
                text = state.channelInputQuery,
                fontSize = 30.sp,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error,
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
            onAction = {},
            onNavigateToChannels = {}
        )
    }
}

@OptIn(UnstableApi::class)
private fun intoMediaItem(
    credintial: Credintial,
    uri: String
): ProgressiveMediaSource {

    val credentials = Credentials.basic(credintial.username, credintial.password)
    val dataSourceFactory = DefaultHttpDataSource.Factory()
        .setDefaultRequestProperties(
            Collections.singletonMap("Authorization", credentials)
        )
        .setConnectTimeoutMs(DefaultHttpDataSource.DEFAULT_CONNECT_TIMEOUT_MILLIS)
        .setReadTimeoutMs(DefaultHttpDataSource.DEFAULT_READ_TIMEOUT_MILLIS)

    val mediaSource = ProgressiveMediaSource.Factory(dataSourceFactory)
        .createMediaSource(MediaItem.fromUri(uri))

    return mediaSource
}
