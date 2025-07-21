package com.edurda77.impuls.tele_tv.channels

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.tooling.preview.Preview
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
            containerColor = Color.Transparent,
            focusedContentColor = Color.Transparent
        ),
        border = ClickableSurfaceDefaults.border(
            border = Border(
                border = BorderStroke(
                    width = 0.dp,
                    color = Color.Transparent
                ),
            ),
            focusedBorder =  Border(
                border = BorderStroke(
                    width = 3.dp,
                    color = MaterialTheme.colorScheme.inversePrimary
                ),)
        )
    ) {
        tvChannel.tvgLogo?.let {
            AsyncImage(
                modifier = modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9),
                model = ImageRequest.Builder(LocalContext.current)
                    .data(it)
                    .decoderFactory(SvgDecoder.Factory())
                    .build(),
                contentDescription = "",
                contentScale = ContentScale.Crop
            )
        }
    }
}

@Preview
@Composable
private fun ChannelItemView(
) {
    ChannelItem(
        tvChannel = TvChannel(
            tvgId = "222",
            tvgLogo = "",
            tvgChannelNumber = 2,
            name = "channel",
            url = ""
        ),
        onClickChannel = {}
    )
}