package com.edurda77.impuls.tele_tv.player


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.edurda77.impuls.tele_tv.domain.model.TvChannel
import com.edurda77.impuls.tele_tv.resources.R
import com.edurda77.impuls.tele_tv.resources.theme.Tele_TvTheme

@Composable
fun ChannelMenu(
    channels: List<TvChannel>,
    selectedIndex: Int?,
    modifier: Modifier = Modifier,
    focusedIndex: Int?
) {
    val scrollState = rememberLazyListState()
    val configuration = LocalWindowInfo.current.containerSize
    val screenWidth = configuration.height.dp
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

    Box(
        modifier = modifier
            .fillMaxHeight()
            .width(screenWidth / 4)
            .background(MaterialTheme.colorScheme.background.copy(alpha = 0.5f))
    ) {
        Column(modifier = modifier.padding(16.dp)) {
            Text(
                text = stringResource(R.string.channels),
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            LazyColumn(
                state = scrollState,
                modifier = modifier
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(15.dp)
            ) {
                items(channels) { channel ->
                    ChannelItem(
                        channel = channel,
                        isCurrent = selectedIndex != null && channel == channels[selectedIndex],
                        isFocused = focusedIndex != null && channel == channels[focusedIndex],
                    )
                }
            }
        }
    }
}

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
    Tele_TvTheme {
        ChannelMenu(
            channels = channels,
            selectedIndex = 1,
            focusedIndex = 2,
        )
    }
}