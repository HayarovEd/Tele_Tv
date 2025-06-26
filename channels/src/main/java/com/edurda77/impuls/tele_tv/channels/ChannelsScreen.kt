package com.edurda77.impuls.tele_tv.channels

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Surface
import androidx.tv.material3.SurfaceDefaults
import androidx.tv.material3.Text
import com.edurda77.impuls.tele_tv.domain.model.TvChannel
import com.edurda77.impuls.tele_tv.resources.R
import com.edurda77.impuls.tele_tv.resources.theme.Tele_TvTheme
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.compose.koinViewModel

@Composable
fun ChannelsScreenRoot(
    viewModel: ChannelsScreenViewModel = koinViewModel(),
    onNavigateTopPlayer: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    BackHandler { }

    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                UiChannelsEvents.PlayerNavigationEvent -> {
                    onNavigateTopPlayer()
                }

                UiChannelsEvents.LoginNavigationEvent -> {
                    onNavigateToLogin()
                }
            }
        }
    }

    ChannelsScreenScreen(
        state = state,
        onAction = viewModel::onAction
    )
}

@Composable
private fun ChannelsScreenScreen(
    modifier: Modifier = Modifier,
    state: ChannelsScreenState,
    onAction: (ChannelsScreenAction) -> Unit,
) {
    val scrollState = rememberLazyGridState()

    /*LaunchedEffect(state.tvChannels) {
        if (state.focusedChannelId != null) {
            *//*val countVisible = scrollState.layoutInfo.visibleItemsInfo.size
            val scrolledIndex = when (state.focusedChannelId) {
                in 0..<countVisible/2  -> 0
                else -> state.focusedChannelId - countVisible/2
            }*//*
            state.scrolledIndex?.let { scrollState.scrollToItem(it) }
        }
    }*/

    /*LaunchedEffect(state.focusedIndex) {
        if (state.focusedIndex >= 0) {
            val countVisible = scrollState.layoutInfo.visibleItemsInfo.size
            val scrolledIndex = when (state.focusedIndex) {
                in 0..<countVisible/2  -> 0
                else -> state.focusedIndex - countVisible/2
            }
            scrollState.animateScrollToItem(scrolledIndex)
        }
    }*/


    Surface(
        modifier = modifier.fillMaxSize(),
        colors = SurfaceDefaults.colors(
            containerColor = MaterialTheme.colorScheme.background
        ),
    ) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.onTertiary,
                            MaterialTheme.colorScheme.onTertiaryContainer
                        )
                    )
                )
                .padding(15.dp),
        ) {
            Row(
                modifier = modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo61),
                    contentDescription = "",
                    contentScale = ContentScale.FillHeight,
                    modifier = modifier
                        .height(50.dp)
                )
                StatusPanel(
                    currentTime = state.currentTime,
                    isEnableUpdate = state.enableUpdate,
                    onUpdateClick = {
                        onAction(ChannelsScreenAction.DownloadUpdate)
                    },
                    onExitClick = {
                        onAction(ChannelsScreenAction.Logout)
                    }
                )
            }
            if (state.isUpdating) {
                Text(
                    modifier = modifier.align(Alignment.CenterHorizontally),
                    text = "${stringResource(R.string.update_downloading)} ${state.percentDownload}%",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.tertiary,
                    textAlign = TextAlign.Center
                )
            } else {
                Spacer(modifier = modifier.height(15.dp))
                Text(
                    modifier = modifier,
                    text = stringResource(R.string.last_showed),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.tertiary,
                )
                Spacer(modifier = modifier.height(15.dp))
                LazyRow(
                    modifier = modifier,
                    horizontalArrangement = Arrangement.spacedBy(20.dp),
                    contentPadding = PaddingValues(10.dp),
                ) {
                    items(state.lastTvChannels) { tvChannel ->
                        ChannelItem(
                            onAction = onAction,
                            modifier = modifier,
                            tvChannel = tvChannel,
                            state = state
                        )
                    }
                }
                Spacer(modifier = modifier.height(15.dp))
                Text(
                    modifier = modifier,
                    text = stringResource(R.string.channels),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.tertiary,
                )
                Spacer(modifier = modifier.height(15.dp))
                LazyVerticalGrid(
                    state = scrollState,
                    horizontalArrangement = Arrangement.spacedBy(20.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp),
                    contentPadding = PaddingValues(10.dp),
                    columns = GridCells.Fixed(6),
                ) {
                    items(state.tvChannels) { tvChannel ->
                        ChannelItem(
                            onAction = onAction,
                            modifier = modifier,
                            tvChannel = tvChannel,
                            state = state
                        )
                    }
                }
            }
            /* if (state.enableUpdate&&!state.isUpdating) {
                 Button(
                     modifier = modifier
                         .align(Alignment.BottomCenter)
                         .padding(vertical = 15.dp, horizontal = 100.dp)
                         .fillMaxWidth(),
                     colors = ButtonDefaults.colors(
                         containerColor = MaterialTheme.colorScheme.secondaryContainer,
                         disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                     ),
                     onClick = {
                         onAction(ChannelsScreenAction.DownloadUpdate)
                     }
                 ) {
                     Text(
                         modifier = modifier.fillMaxWidth(),
                         text = stringResource(R.string.enable_update),
                         style = MaterialTheme.typography.bodyMedium,
                         color = MaterialTheme.colorScheme.onSecondaryContainer,
                         textAlign = TextAlign.Center
                     )
                 }
             }*/
        }
    }
}

@Preview
@Composable
private fun Preview() {
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
        ChannelsScreenScreen(
            state = ChannelsScreenState(
                tvChannels = channels,
                focusedChannelId = "0"
            ),
            onAction = {}
        )
    }
}

@Preview()
@Composable
private fun Preview2() {
    val channels = (1..10).map {
        TvChannel(
            tvgId = "www",
            tvgLogo = "",
            tvgChannelNumber = it,
            name = "Channel",
            url = ""
        )
    }
    Tele_TvTheme {
        ChannelsScreenScreen(
            state = ChannelsScreenState(
                tvChannels = channels,
                focusedChannelId = "0",
                enableUpdate = true
            ),
            onAction = {}
        )
    }
}

@Preview()
@Composable
private fun Preview3() {
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
        ChannelsScreenScreen(
            state = ChannelsScreenState(
                tvChannels = channels,
                focusedChannelId = "0",
                enableUpdate = true,
                isUpdating = true
            ),
            onAction = {}
        )
    }
}