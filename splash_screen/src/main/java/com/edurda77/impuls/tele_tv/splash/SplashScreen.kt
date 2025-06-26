package com.edurda77.impuls.tele_tv.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.edurda77.impuls.tele_tv.resources.R
import com.edurda77.impuls.tele_tv.resources.theme.Tele_TvTheme
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.compose.koinViewModel

@Composable
fun SplashScreenRoot(
    viewModel: SplashScreenViewModel = koinViewModel(),
    onNavigateToChannels: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                UiSplashEvents.ChannelsNavigationEvent -> onNavigateToChannels()
                UiSplashEvents.LoginNavigationEvent -> onNavigateToLogin()
            }
        }
    }

    SplashScreenScreen()
}

@Composable
private fun SplashScreenScreen(
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxSize()) {
        Image(
            modifier = modifier
                .fillMaxSize(),
            painter = painterResource(id = R.drawable.scale_1200),
            contentScale = ContentScale.FillBounds,
            contentDescription = "",
        )
        Image(
            modifier = modifier
                .align(Alignment.Center)
                .fillMaxWidth()
                .padding(60.dp),
            painter = painterResource(id = R.drawable.logo62),
            contentScale = ContentScale.FillWidth,
            contentDescription = "",
        )
    }
}

@Preview(device = Devices.AUTOMOTIVE_1024p, widthDp = 1280, heightDp = 720)
@Composable
private fun Preview() {
    Tele_TvTheme {
        SplashScreenScreen()
    }
}