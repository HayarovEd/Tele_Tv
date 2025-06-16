package com.edurda77.impuls.tele_tv.channels

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.tv.material3.Text
import com.edurda77.impuls.tele_tv.resources.theme.Tele_TvTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun ChannelsScreenRoot(
    viewModel: ChannelsScreenViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

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
    Text(text = "this channel list")
}

@Preview
@Composable
private fun Preview() {
    Tele_TvTheme {
        ChannelsScreenScreen(
            state = ChannelsScreenState(),
            onAction = {}
        )
    }
}