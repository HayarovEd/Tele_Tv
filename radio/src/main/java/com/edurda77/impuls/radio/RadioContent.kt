package com.edurda77.impuls.radio

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
import kotlinx.coroutines.delay

@Composable
fun RadioContent(
    modifier: Modifier = Modifier,
    radio: TvChannel,
    audioSession: Int
) {
    var track by remember { mutableStateOf("") }

    LaunchedEffect(true) {
        while (true) {
            track = RadioMetadataParser().getCurrentTrack(radio.url)?:""
            delay(5000)
        }
    }
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
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
        Spacer(modifier = modifier.height(15.dp))
        Text(
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
            audioSession = 0
        )
    }
}