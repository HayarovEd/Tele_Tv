package com.edurda77.impuls.tele_tv.player

import androidx.annotation.OptIn
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.media3.ui.compose.PlayerSurface
import androidx.media3.ui.compose.SURFACE_TYPE_TEXTURE_VIEW
import androidx.media3.ui.compose.modifiers.resizeWithContentScale
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

    /*val videoPlayerState = rememberVideoPlayerState(
        hideSeconds = 4,
    )*/

    LaunchedEffect(exoPlayer, state.tvChannels, state.selectedIndex) {
        state.credintial?.let {
            exoPlayer.setMediaSource(intoMediaItem(
                credintial = state.credintial,
                uri = state.tvChannels[state.selectedIndex].url
            ))
            exoPlayer.prepare()
        }
    }

    Box(
        modifier
            .focusable()
    ) {
        PlayerSurface(
            player = exoPlayer,
            surfaceType = SURFACE_TYPE_TEXTURE_VIEW,
            modifier = modifier.resizeWithContentScale(
                contentScale = ContentScale.Fit,
                sourceSizeDp = null
            )
        )
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
            Collections.singletonMap("Authorization", credentials))
        .setConnectTimeoutMs(DefaultHttpDataSource.DEFAULT_CONNECT_TIMEOUT_MILLIS)
        .setReadTimeoutMs(DefaultHttpDataSource.DEFAULT_READ_TIMEOUT_MILLIS)

    val mediaSource = ProgressiveMediaSource.Factory(dataSourceFactory)
        .createMediaSource(MediaItem.fromUri(uri))

    return mediaSource
}
