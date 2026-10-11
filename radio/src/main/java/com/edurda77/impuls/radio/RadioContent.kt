package com.edurda77.impuls.radio

import androidx.annotation.OptIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import coil.compose.AsyncImage
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.edurda77.impuls.tele_tv.domain.model.TvChannel
import com.edurda77.impuls.tele_tv.domain.utils.RADIO_CATEGORY
import com.edurda77.impuls.tele_tv.domain.utils.RADIO_IMAGE_URL
import com.edurda77.impuls.tele_tv.domain.utils.RADIO_NAME
import com.edurda77.impuls.tele_tv.resources.theme.Tele_TvTheme

@OptIn(UnstableApi::class)
@Composable
fun RadioContent(
    modifier: Modifier = Modifier,
    radio: TvChannel,
    player: Player?
) {
    var track by remember { mutableStateOf("") }

    val audioSession = (player as? ExoPlayer)?.audioSessionId ?: 0

    DisposableEffect(player) {
        val listener = object : Player.Listener {
            override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) {
                track = mediaMetadata.title?.toString()
                    ?: mediaMetadata.displayTitle?.toString()
                    ?: ""
            }
        }
        player?.addListener(listener)
        // Initial update
        player?.mediaMetadata?.let {
            track = it.title?.toString() ?: it.displayTitle?.toString() ?: ""
        }
        onDispose {
            player?.removeListener(listener)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        WithPermission {
            if (!LocalInspectionMode.current) {
                val context = LocalContext.current
                val primaryColor = MaterialTheme.colorScheme.primary.toArgb()
                val customVis = remember { CustomVisualisation(context) }

                AndroidView(
                    modifier = Modifier.fillMaxSize(),
                    factory = {
                        customVis
                    },
                    update = { view ->
                        view.setColor(primaryColor)
                        view.setPlayer(audioSession)
                    }
                )

                DisposableEffect(customVis) {
                    onDispose {
                        customVis.release()
                    }
                }
            }
        }
        /*AndroidView(
            modifier = Modifier
                .fillMaxHeight(0.7f)
                .aspectRatio(1f),
            factory = {
                circleBarVisualizer
            })*/
        AsyncImage(
            modifier = Modifier
                .fillMaxHeight(0.5f)
                .aspectRatio(1f),
            model = ImageRequest.Builder(LocalContext.current)
                .data(radio.tvgLogo)
                .decoderFactory(SvgDecoder.Factory())
                .build(),
            contentDescription = "",
            contentScale = ContentScale.FillHeight
        )
        Text(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 30.dp),
            text = track,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Preview
@Composable
private fun Preview() {
    Tele_TvTheme {
        RadioContent(
            radio = TvChannel(
                tvgId = "111",
                tvgLogo = RADIO_IMAGE_URL,
                tvgChannelNumber = 1,
                name = RADIO_NAME,
                url = "",
                categoryIds = listOf(RADIO_CATEGORY),
                isRadio = true
            ),
            player = null
        )
    }
}