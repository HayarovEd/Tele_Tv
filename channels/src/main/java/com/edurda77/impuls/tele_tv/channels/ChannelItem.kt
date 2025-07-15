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
    modifier: Modifier = Modifier,
    tvChannel: TvChannel,
    onClickChannel: () -> Unit
) {
    val configuration = LocalWindowInfo.current.containerSize
    val screenWidth = configuration.height.dp
    Surface(
        onClick = onClickChannel,
        modifier = modifier
            .width(screenWidth / 8)
            .aspectRatio(16f / 9),
        colors = ClickableSurfaceDefaults.colors(
            containerColor = MaterialTheme.colorScheme.primary.copy(
                alpha = 0.5f
            ),
            focusedContentColor = MaterialTheme.colorScheme.primary
        ),
        border = ClickableSurfaceDefaults.border(
            /*border = Border(
                border = BorderStroke(
                    width = 3.dp,
                    color = Color.Transparent
                ),
                inset = 4.dp
            ),*/
            focusedBorder =  Border(
                border = BorderStroke(
                    width = 3.dp,
                    color = MaterialTheme.colorScheme.inversePrimary
                ),)
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
        }
    }
}