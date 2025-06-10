package com.edurda77.impuls.tele_tv.player

import android.view.KeyEvent
import androidx.annotation.OptIn
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.media3.ui.compose.PlayerSurface
import androidx.media3.ui.compose.SURFACE_TYPE_TEXTURE_VIEW
import androidx.media3.ui.compose.modifiers.resizeWithContentScale
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import coil.compose.AsyncImage
import com.edurda77.impuls.tele_tv.domain.model.Credintial
import com.edurda77.impuls.tele_tv.resources.theme.Tele_TvTheme
import okhttp3.Credentials
import org.koin.androidx.compose.koinViewModel
import java.util.Collections

@Composable
fun PlayerScreenRoot(
    viewModel: PlayerScreenViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    PlayerScreenScreen(
        state = state,
        onAction = viewModel::onAction
    )
}

@OptIn(UnstableApi::class)
@Composable
fun PlayerScreenScreen(
    modifier: Modifier = Modifier,
    state: PlayerScreenState,
    onAction: (PlayerScreenAction) -> Unit,
) {
    val context = LocalContext.current
    val exoPlayer = rememberPlayer(context)

    val focusRequester = remember { FocusRequester() }
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused = interactionSource.collectIsFocusedAsState().value
   // var isMenuVisible by remember { mutableStateOf(false) }

    LaunchedEffect(exoPlayer, state.tvChannels, state.selectedIndex) {
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
    Box(

    ) {
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
                        when (it.nativeKeyEvent.keyCode) {
                            KeyEvent.KEYCODE_DPAD_DOWN -> {
                                onAction(PlayerScreenAction.IncrimentTvChannel)
                            }

                            KeyEvent.KEYCODE_DPAD_UP -> {
                                onAction(PlayerScreenAction.DecrimentTvChannel)
                            }

                            KeyEvent.KEYCODE_DPAD_RIGHT -> {
                                onAction(PlayerScreenAction.OnShowTitle)
                            }

                            KeyEvent.KEYCODE_DPAD_LEFT -> {
                                onAction(PlayerScreenAction.ShowSideMenu)
                                // onChangeFocus(FocusDirection.Exit)
                            }
                            KeyEvent.KEYCODE_ENTER -> {
                                // onChangeFocus(FocusDirection.Exit)
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
            modifier = modifier.align(Alignment.TopStart),
            enter = slideInVertically { it },
            exit = slideOutVertically { it }
        ) {
            Row(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (state.tvChannels.isNotEmpty()) {
                    state.tvChannels[state.selectedIndex].tvgLogo?.let {
                        AsyncImage(
                            modifier = modifier.size(50.dp),
                            model = state.tvChannels[state.selectedIndex].tvgLogo,
                            contentDescription = "",
                            contentScale = ContentScale.Fit
                        )
                    }
                    Spacer(modifier = modifier.width(15.dp))
                    Text(
                        modifier = modifier,
                        text = state.tvChannels[state.selectedIndex].tvgChno,
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
        if (state.isVisibleSideMenu) {
            ChannelMenu(
                channels = state.tvChannels,
                selectIndex = { index ->
                    onAction(PlayerScreenAction.UpdateSelectedIndex(index))
                },
                selectedIndex = state.selectedIndex,
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
            onAction = {}
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
