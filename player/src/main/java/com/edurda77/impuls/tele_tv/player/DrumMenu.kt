package com.edurda77.impuls.tele_tv.player

import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import coil.compose.AsyncImage
import com.edurda77.impuls.tele_tv.domain.model.TvChannel
import com.edurda77.impuls.tele_tv.domain.model.TvEpg
import com.edurda77.impuls.tele_tv.resources.theme.Tele_TvTheme
import kotlin.time.Clock


@Composable
fun DrumMenu(
    modifier: Modifier = Modifier,
    channel: TvChannel,
    tvEpg: TvEpg?,
    currentTime: Long,
    baseTextSize: TextUnit = 30.sp,
) {
    val configuration = LocalWindowInfo.current.containerSize
    val screenWidth = configuration.width.dp

    Column(
        modifier = modifier
            .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f))
            .width(screenWidth / 2)
            .padding(8.dp),
    ) {
        Row(
            modifier = modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            channel.tvgLogo?.let {
                AsyncImage(
                    modifier = modifier
                        .size(30.dp)
                        .clip(CircleShape),
                    model = channel.tvgLogo,
                    contentDescription = "",
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = modifier.width(15.dp))
            }
            Text(
                modifier = modifier
                    .padding(vertical = 10.dp)
                    .basicMarquee(),
                text = "${channel.tvgChannelNumber}, ${channel.name}",
                fontSize = baseTextSize,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                maxLines = 1
            )
        }
        Spacer(modifier = modifier.height(5.dp))
        tvEpg?.let {
            ItemTvEpg(
                tvEpg = tvEpg,
                currentTime = currentTime
            )
        }

    }
}

@Preview(
      showBackground = true
)
@Composable
private fun DrumMenuView() {
    val channels = (0..15).map {
        TvChannel(
            tvgId = "www",
            tvgLogo = "",
            tvgChannelNumber = it,
            name = "channel",
            url = "",
            categoryIds = listOf("33")
        )
    }
    Tele_TvTheme {
        DrumMenu(
            channel = channels.first(),
            tvEpg = TvEpg(
                channelName = "Рифей (22)",
                channelNumber = "22",
                channelUuid = "bb96aabb80ee7139983ed081a341d148",
                ageRating = 16,
                description = "Девочка Маша и Медведь — неразлучные друзья. В голову озорной Маши всегда приходят самые невероятные идеи, и поэтому каждый день героев наполнен весельем, приключениями и новыми открытиями.",
                eventId = 464049,
                start = 1750806000,
                stop = 1750845600,
                title = "Новости на Рифее."
            ),
            currentTime = Clock.System.now().epochSeconds,
        )
    }
}