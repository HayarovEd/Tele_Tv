package com.edurda77.impuls.tele_tv.channels

import android.health.connect.datatypes.Device
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Surface
import androidx.tv.material3.SurfaceDefaults
import androidx.tv.material3.Tab
import androidx.tv.material3.TabDefaults
import androidx.tv.material3.TabRow
import androidx.tv.material3.Text
import com.edurda77.impuls.tele_tv.domain.model.Category
import com.edurda77.impuls.tele_tv.domain.model.TvChannel
import com.edurda77.impuls.tele_tv.domain.utils.createCategoryChannelMap
import com.edurda77.impuls.tele_tv.resources.R
import com.edurda77.impuls.tele_tv.resources.model.TypeFactory
import com.edurda77.impuls.tele_tv.resources.theme.Tele_TvTheme
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.compose.koinViewModel

@Composable
fun ChannelsScreenRoot(
    viewModel: ChannelsScreenViewModel = koinViewModel(),
    typeFactory: TypeFactory,
    onNavigateToPlayer: (String) -> Unit,
    onNavigateToLogin: () -> Unit,
) {
    BackHandler { }

    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {

                UiChannelsEvents.LoginNavigationEvent -> {
                    onNavigateToLogin()
                }

                is UiChannelsEvents.PlayerNavigationEvent -> {
                    onNavigateToPlayer(event.channelId)
                }
            }
        }
    }

    ChannelsScreenScreen(
        state = state,
        typeFactory = typeFactory,
        onAction = viewModel::onAction,
    )
}

@Composable
private fun ChannelsScreenScreen(
    modifier: Modifier = Modifier,
    typeFactory: TypeFactory,
    state: ChannelsScreenState,
    onAction: (ChannelsScreenAction) -> Unit,
) {
    val scrollState = rememberLazyGridState()
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp
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
                .padding(25.dp),
        ) {
            Row(
                modifier = modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Image(
                    painter = when (typeFactory) {
                        TypeFactory.TELE -> painterResource(id = R.drawable.logo61)
                        TypeFactory.PTK -> painterResource(id = R.drawable.ptk_logo_crop)
                    },
                    contentDescription = "",
                    contentScale = ContentScale.FillHeight,
                    modifier = modifier
                        .height(50.dp)
                )
                StatusPanel(
                    currentTime = state.currentTime,
                    user = state.credintial?.username?:"",
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
                Box(
                    modifier = modifier
                        .fillMaxHeight()
                        .align(Alignment.CenterHorizontally),
                ) {
                    Text(
                        modifier = modifier.align(alignment = Alignment.Center),
                        text = "${stringResource(R.string.update_downloading)} ${state.percentDownload}%",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.tertiary,
                        textAlign = TextAlign.Center
                    )
                }
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
                            modifier = modifier,
                            tvChannel = tvChannel,
                            onClickChannel = {
                                onAction(ChannelsScreenAction.SaveChosenChannel(tvChannel))
                            }
                        )
                    }
                }
                Spacer(modifier = modifier.height(15.dp))
                Row(
                    modifier = modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.Start
                ) {
                    TabRow(
                        modifier = modifier
                            .width(screenWidth-30.dp),
                        selectedTabIndex = -1
                    ) {
                        state.groupedTvChannels.keys.forEach {
                            val interactionSource = remember { MutableInteractionSource() }
                            val isFocused by interactionSource.collectIsFocusedAsState()
                            Tab(
                                modifier = modifier
                                    .padding(horizontal = 8.dp)
                                    .border(
                                        width = if (isFocused) 3.dp else 1.dp,
                                        color = if (state.selectedTabIndex == it ) MaterialTheme.colorScheme.onBackground else Color.Transparent,
                                        shape = MaterialTheme.shapes.extraLarge
                                    )
                                    .background(
                                        color = when {
                                            isFocused -> MaterialTheme.colorScheme.inverseSurface
                                            /*state.selectedTabIndex == it -> MaterialTheme.colorScheme.surfaceVariant.copy(
                                                alpha = 0.5f
                                            )*/

                                            else -> Color.Transparent
                                        },
                                        shape = MaterialTheme.shapes.extraLarge
                                    )
                                    .padding(horizontal = 10.dp, vertical = 5.dp),
                                selected = state.selectedTabIndex == it,
                                colors = TabDefaults.pillIndicatorTabColors(
                                    selectedContentColor = MaterialTheme.colorScheme.onSurface,
                                    focusedContentColor = MaterialTheme.colorScheme.surface,
                                ),
                                interactionSource = interactionSource,
                                onClick = {

                                },
                                onFocus = {
                                    onAction(ChannelsScreenAction.UpdateSelectedTabIndex(it))
                                }
                            ) {

                                Text(
                                    modifier = modifier
                                        .basicMarquee(),
                                    text = it.name,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontSize = if (isFocused) 25.sp else 22.sp
                                    ),
                                    color = when {
                                        isFocused -> MaterialTheme.colorScheme.inverseOnSurface
                                        else -> MaterialTheme.colorScheme.tertiary
                                    },
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = modifier.height(15.dp))
                LazyVerticalGrid(
                    state = scrollState,
                    horizontalArrangement = Arrangement.spacedBy(20.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp),
                    contentPadding = PaddingValues(10.dp),
                    columns = GridCells.Fixed(6),
                ) {
                    items(
                        items = state.groupedTvChannels[state.selectedTabIndex]?:emptyList(),
                        key = {
                            it.tvgId
                        }
                    ) { tvChannel ->
                        ChannelItem(
                            modifier = modifier,
                            tvChannel = tvChannel,
                            onClickChannel = {
                                onAction(ChannelsScreenAction.SaveChosenChannel(tvChannel))
                            }
                        )
                    }
                }
            }
        }
    }
}

@Preview(
    device = Devices.TV_1080p
)
@Composable
private fun Preview() {
    val categories = (1..5).map {
        Category(
            key = "$it",
            name = "category $it"
        )
    }
    val channels = (1..10).map {
        val ct = categories[it % categories.size].key
        TvChannel(
            tvgId = "id$it",
            tvgLogo = "",
            tvgChannelNumber = it,
            name = "channel",
            url = "",
            categoryIds = listOf(ct)
        )
    }
    Tele_TvTheme {
        ChannelsScreenScreen(
            state = ChannelsScreenState(
                tvChannels = channels,
                categories = categories,
                groupedTvChannels = createCategoryChannelMap(
                    categories = categories,
                    channels = channels
                ),
                lastTvChannels = channels,
                selectedTabIndex = categories.first()
            ),
            onAction = {},
            typeFactory = TypeFactory.TELE
        )
    }
}

@Preview(
    device = Devices.TV_1080p
)
@Composable
private fun Preview2() {
    val channels = (1..10).map {
        TvChannel(
            tvgId = "id$it",
            tvgLogo = "",
            tvgChannelNumber = it,
            name = "Channel",
            url = "",
            categoryIds = listOf("33")
        )
    }
    Tele_TvTheme {
        ChannelsScreenScreen(
            state = ChannelsScreenState(
                tvChannels = channels,
                // focusedChannelId = "id1",
                enableUpdate = true,
                lastTvChannels = channels
            ),
            onAction = {},
            typeFactory = TypeFactory.PTK
        )
    }
}

@Preview(
    device = Devices.TV_1080p
)
@Composable
private fun Preview3() {
    val channels = (1..10).map {
        TvChannel(
            tvgId = "id$it",
            tvgLogo = "",
            tvgChannelNumber = it,
            name = "channel",
            url = "",
            categoryIds = listOf("33")
        )
    }
    Tele_TvTheme {
        ChannelsScreenScreen(
            state = ChannelsScreenState(
                tvChannels = channels,
                //  focusedChannelId = "id1",
                enableUpdate = true,
                isUpdating = true,
                lastTvChannels = channels
            ),
            onAction = {},
            typeFactory = TypeFactory.TELE
        )
    }
}