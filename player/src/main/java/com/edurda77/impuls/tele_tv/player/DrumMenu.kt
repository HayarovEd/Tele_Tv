package com.edurda77.impuls.tele_tv.player

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import coil.compose.AsyncImage
import com.edurda77.impuls.tele_tv.domain.model.TvChannel
import com.edurda77.impuls.tele_tv.resources.theme.Tele_TvTheme


@Composable
fun DrumMenu(
    modifier: Modifier = Modifier,
    channels: List<TvChannel>,
    selectedIndex: Int,
    baseTextSize: TextUnit = 30.sp,
) {
    val scrollState = rememberLazyListState()
    var countVisible by remember { mutableIntStateOf(0) }
    var firstVisibleItemIndex by remember { mutableIntStateOf(0) }

    LaunchedEffect(selectedIndex) {
        if (selectedIndex >= 0) {
            firstVisibleItemIndex = scrollState.firstVisibleItemIndex
            countVisible = scrollState.layoutInfo.visibleItemsInfo.size
            val scrolledIndex = when (selectedIndex) {
                in 0..<countVisible / 2 -> 0
                else -> selectedIndex - countVisible / 2
            }
            scrollState.scrollToItem(scrolledIndex)
        }
    }

    Box(
        modifier = modifier
            .fillMaxHeight()
            .background(color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
        contentAlignment = Alignment.CenterEnd
    ) {
        LazyColumn(
            //state = scrollState,
            modifier = modifier
                .padding(16.dp),
            state = scrollState,
            verticalArrangement = Arrangement.spacedBy(15.dp)
        ) {

            itemsIndexed(channels) { index, channel ->
                Row(
                    modifier = modifier
                        .border(
                            width = if (index == selectedIndex) 3.dp else 0.dp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    channel.tvgLogo?.let {
                        AsyncImage(
                            modifier = modifier.size(50.dp),
                            model = channel.tvgLogo,
                            contentDescription = "",
                            contentScale = ContentScale.Fit
                        )
                    }
                    Spacer(modifier = modifier.width(15.dp))
                    Text(
                        modifier = modifier,
                        text = channel.tvgChno,
                        fontSize = baseTextSize,
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                }
            }
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
            tvgChno = "$it channel",
            name = "channel",
            url = ""
        )
    }
    Tele_TvTheme {
        DrumMenu(
            channels = channels,
            selectedIndex = 4,
        )
    }
}