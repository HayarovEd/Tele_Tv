package com.edurda77.impuls.tele_tv.channels

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.tv.material3.Border
import androidx.tv.material3.ClickableSurfaceDefaults
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Surface
import androidx.tv.material3.SurfaceDefaults
import androidx.tv.material3.Text
import coil.compose.AsyncImage
import com.edurda77.impuls.tele_tv.domain.model.TvChannel
import com.edurda77.impuls.tele_tv.resources.theme.Tele_TvTheme
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.compose.koinViewModel

@Composable
fun ChannelsScreenRoot(
    viewModel: ChannelsScreenViewModel = koinViewModel(),
    onNavigateTopPlayer: () -> Unit
) {
    BackHandler {  }

    val state by viewModel.state.collectAsStateWithLifecycle()

    Log.d("REST TELE TV", "focused index ${state.focusedIndex}")

    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                UiChannelsEvents.PlayerNavigationEvent -> {
                    onNavigateTopPlayer()
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
    val scrollState = rememberLazyListState()

    LaunchedEffect(state.tvChannels) {
        if (state.focusedIndex >= 0) {
            val countVisible = scrollState.layoutInfo.visibleItemsInfo.size
            val scrolledIndex = when (state.focusedIndex) {
                in 0..<countVisible/2  -> 0
                else -> state.focusedIndex - countVisible/2
            }
            scrollState.scrollToItem(state.focusedIndex)
        }
    }

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
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.onTertiary,
                            MaterialTheme.colorScheme.onTertiaryContainer
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            LazyRow(
                state = scrollState,
                horizontalArrangement = Arrangement.spacedBy(20.dp),
                contentPadding = PaddingValues(20.dp),
            ) {
                itemsIndexed(state.tvChannels) { index, tvChannel ->
                    Surface(
                        onClick = {
                            onAction(ChannelsScreenAction.SaveSelectedChannel)
                        },
                        modifier = modifier
                            .width(200.dp)
                            .aspectRatio(9f / 16)
                            .onFocusChanged {
                                if (it.hasFocus) {
                                    onAction(ChannelsScreenAction.UpdateFocusedIndex(index))
                                }
                            },
                        colors = ClickableSurfaceDefaults.colors(
                            containerColor = if (tvChannel == state.tvChannels[state.focusedIndex]) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primary.copy(
                                alpha = 0.5f
                            ),
                        ),
                        border = ClickableSurfaceDefaults.border(
                            border = Border(
                                border = BorderStroke(
                                    width = 3.dp,
                                    color = if (tvChannel == state.tvChannels[state.focusedIndex]) MaterialTheme.colorScheme.secondaryContainer else Color.Transparent
                                ),
                                inset = 4.dp
                            )
                        )
                    ) {
                        Column(
                            modifier = modifier
                                .fillMaxHeight()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            tvChannel.tvgLogo?.let {
                                AsyncImage(
                                    modifier = modifier.size(150.dp),
                                    model = tvChannel.tvgLogo,
                                    contentDescription = "",
                                    contentScale = ContentScale.Fit
                                )
                                Spacer(modifier = modifier.height(15.dp))
                            }
                            Text(
                                modifier = modifier.fillMaxWidth(),
                                text = tvChannel.tvgChno,
                                style = MaterialTheme.typography.headlineMedium,
                                color = MaterialTheme.colorScheme.onPrimary,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview()
@Composable
private fun Preview() {
    val channels = (1..10).map {
        TvChannel(
            tvgId = "www",
            tvgLogo = "",
            tvgChno = "$it channel",
            name = "channel",
            url = ""
        )
    }
    Tele_TvTheme {
        ChannelsScreenScreen(
            state = ChannelsScreenState(
                tvChannels = channels,
                focusedIndex = 0
            ),
            onAction = {}
        )
    }
}