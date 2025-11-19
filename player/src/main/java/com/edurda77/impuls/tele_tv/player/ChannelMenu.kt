package com.edurda77.impuls.tele_tv.player


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.edurda77.impuls.tele_tv.domain.model.TvChannel
import com.edurda77.impuls.tele_tv.domain.model.TvEpg
import com.edurda77.impuls.tele_tv.resources.R
import com.edurda77.impuls.tele_tv.resources.theme.Tele_TvTheme
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@Composable
fun ChannelMenu(
    channels: ImmutableList<TvChannel>,
    allTvEpg: Map<String, TvEpg>,
    selectedIndex: Int?,
    modifier: Modifier = Modifier,
    heightItem: Dp,
    focusedIndex: Int?,
    currentTime: Long,
) {
    val scrollState = rememberLazyListState()

    LaunchedEffect(focusedIndex) {
        if (focusedIndex != null && focusedIndex >= 0) {
            val countVisible = scrollState.layoutInfo.visibleItemsInfo.size
            val scrolledIndex = when (focusedIndex) {
                in 0..<countVisible / 2 -> 0
                else -> focusedIndex - countVisible / 2
            }
            scrollState.scrollToItem(scrolledIndex)
        }
    }
    Column(modifier = modifier.padding(16.dp)) {
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(R.string.channels),
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        LazyColumn(
            state = scrollState,
            modifier = modifier
                .fillMaxWidth(),
            contentPadding = PaddingValues(vertical = 5.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(
                items = channels,
                key = {
                    it.tvgChannelNumber
                }) { channel ->
                ChannelItem(
                    channel = channel,
                    isCurrent = selectedIndex != null && channel == channels[selectedIndex],
                    isFocused = focusedIndex != null && channel == channels[focusedIndex],
                    tvEpg = allTvEpg[channel.tvgId],
                    currentTime = currentTime,
                    height = heightItem
                )
            }
        }
    }
}

@OptIn(ExperimentalTime::class)
@Preview
@Composable
private fun ChannelMenuView() {

    val channels = (1..10).map {
        TvChannel(
            tvgId = "www",
            tvgLogo = "",
            tvgChannelNumber = it,
            name = "channel",
            url = ""
        )
    }
    val epgs = (1..10).map {
        TvEpg(
            channelName = "Рифей (22)",
            channelNumber = "$it",
            channelUuid = "bb96aabb80ee7139983ed081a341d148",
            ageRating = 16,
            description = "Девочка Маша и Медведь — неразлучные друзья. В голову озорной Маши всегда приходят самые невероятные идеи, и поэтому каждый день героев наполнен весельем, приключениями и новыми открытиями.",
            eventId = 464049,
            start = 1750806000,
            stop = 1750845600,
            title = "Новости на Рифее."
        )
    }
    Tele_TvTheme {
        ChannelMenu(
            channels = channels.toImmutableList(),
            selectedIndex = 1,
            focusedIndex = 0,
            allTvEpg = epgs.associateBy { it.channelUuid },
            heightItem = 40.dp,
            currentTime = Clock.System.now().epochSeconds,
        )
    }
}