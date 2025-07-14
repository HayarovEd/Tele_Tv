package com.edurda77.impuls.tele_tv.channels

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Border
import androidx.tv.material3.ClickableSurfaceDefaults
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Surface
import coil.compose.AsyncImage
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.edurda77.impuls.tele_tv.domain.model.TvChannel

@Composable
fun ChannelItem(
    onAction: (ChannelsScreenAction) -> Unit,
    modifier: Modifier = Modifier,
    tvChannel: TvChannel,
    state: ChannelsScreenState
) {
    val configuration = LocalWindowInfo.current.containerSize
    val screenWidth = configuration.height.dp
    Surface(
        onClick = {
            onAction(ChannelsScreenAction.SaveSelectedChannel)
        },
        modifier = modifier
            .width(screenWidth / 8)
            .aspectRatio(16f / 9)
            .onFocusChanged {
                if (it.hasFocus) {
                    onAction(ChannelsScreenAction.UpdateFocusedIndex(tvChannel.tvgId))
                }
            },
        colors = ClickableSurfaceDefaults.colors(
            containerColor = if (state.scrolledIndex != null && tvChannel == state.tvChannels[state.scrolledIndex]) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primary.copy(
                alpha = 0.5f
            ),
        ),
        border = ClickableSurfaceDefaults.border(
            border = Border(
                border = BorderStroke(
                    width = 3.dp,
                    color = if (state.scrolledIndex != null && tvChannel == state.tvChannels[state.scrolledIndex]) MaterialTheme.colorScheme.secondaryContainer else Color.Transparent
                ),
                inset = 4.dp
            )
        )
    ) {
        Box(
            modifier = modifier
                .fillMaxSize(),
        ) {
            tvChannel.tvgLogo?.let {
                AsyncImage(
                    modifier = modifier.fillMaxSize(),
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(it)
                        .decoderFactory(SvgDecoder.Factory())
                        .build(),
                    contentDescription = "",
                )
                Spacer(modifier = modifier.height(15.dp))
            }
            /*Text(
                modifier = modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .basicMarquee(),
                text = tvChannel.name,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onPrimary,
                textAlign = TextAlign.Center,
                maxLines = 1
            )*/
        }
    }
}