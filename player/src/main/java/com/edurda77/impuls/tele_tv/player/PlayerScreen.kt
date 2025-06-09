package com.edurda77.impuls.tele_tv.player

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.edurda77.impuls.tele_tv.resources.theme.Tele_TvTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun PlayerScreenRoot(
    viewModel: PlayerScreenViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    PlayerScreenScreen(
        state = state,
        onAction = viewModel::onAction
    )
}

@Composable
fun PlayerScreenScreen(
    modifier: Modifier = Modifier,
    state: PlayerScreenState,
    onAction: (PlayerScreenAction) -> Unit,
) {

}

@Preview
@Composable
private fun Preview() {
    Tele_TvTheme {
        PlayerScreenScreen(
            state = PlayerScreenState(),
            onAction = {}
        )
    }
}