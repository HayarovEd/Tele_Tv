package com.edurda77.impuls.tele_tv.player

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.focusable
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Border
import androidx.tv.material3.Card
import androidx.tv.material3.CardDefaults
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import coil.compose.AsyncImage
import com.edurda77.impuls.tele_tv.domain.model.TvChannel
import com.edurda77.impuls.tele_tv.domain.model.TvEpg
import com.edurda77.impuls.tele_tv.resources.theme.Tele_TvTheme
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@Composable
fun ChannelItem(
    channel: TvChannel,
    isCurrent: Boolean,
    isFocused: Boolean,
    tvEpg: TvEpg?,
    currentTime: Long,
    modifier: Modifier = Modifier
) {

    Card(
        modifier = modifier
            .focusable(),
        border = CardDefaults.border(
            border = Border(
                border = BorderStroke(
                    color = if (isFocused) MaterialTheme.colorScheme.inversePrimary else Color.Transparent,
                    width = 3.dp
                ),
            )
        ),
        colors = CardDefaults.colors(
            containerColor = if (isCurrent) MaterialTheme.colorScheme.primaryContainer
            else MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
        ),
        onClick = {  }
    ) {
        Column (
            modifier = modifier
                .fillMaxWidth()
                .padding(8.dp),
        ){
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                channel.tvgLogo?.let {
                    AsyncImage(
                        modifier = modifier
                            .size(40.dp)
                            .clip(CircleShape),
                        model = channel.tvgLogo,
                        contentDescription = "",
                        contentScale = ContentScale.Crop
                    )
                }
                Spacer(modifier = modifier.width(15.dp))
                Text(
                    modifier = modifier,
                    text = "${channel.tvgChannelNumber} ",
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isCurrent) MaterialTheme.colorScheme.onPrimaryContainer
                    else MaterialTheme.colorScheme.onSurface,
                )
                Spacer(modifier = modifier.width(1.dp))
                Text(
                    modifier = modifier.basicMarquee(),
                    text = channel.name,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isCurrent) MaterialTheme.colorScheme.onPrimaryContainer
                    else MaterialTheme.colorScheme.onSurface,
                )
            }
            Spacer(modifier = modifier.height(5.dp))
            tvEpg?.let { epg->
                ItemTvEpg(
                    tvEpg = epg,
                    currentTime = currentTime
                )
            }
        }
    }
}

@OptIn(ExperimentalTime::class)
@Preview
@Composable
private fun ChannelItemView() {
    Tele_TvTheme {
        ChannelItem(
            channel = TvChannel(
                tvgId = "111",
                tvgLogo = "",
                tvgChannelNumber = 1,
                name = "First",
                url = ""
            ),
            isCurrent = true,
            isFocused = false,
            currentTime = Clock.System.now().epochSeconds,
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
            )
        )
    }
}

@OptIn(ExperimentalTime::class)
@Preview
@Composable
private fun ChannelItemView2() {
    Tele_TvTheme {
        ChannelItem(
            channel = TvChannel(
                tvgId = "111",
                tvgLogo = "",
                tvgChannelNumber = 1,
                name = "First",
                url = ""
            ),
            isCurrent = false,
            isFocused = true,
            currentTime = Clock.System.now().epochSeconds,
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
            )
        )
    }
}