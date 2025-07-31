package com.edurda77.impuls.tele_tv.splash

import android.widget.ImageView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import com.edurda77.impuls.tele_tv.resources.R
import com.edurda77.impuls.tele_tv.resources.theme.Tele_TvTheme
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.compose.koinViewModel

@Composable
fun SplashScreenRoot(
    viewModel: SplashScreenViewModel = koinViewModel(),
    onNavigateToChannels: () -> Unit,
    onNavigateToChannelsMobile: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onNavigateToLoginMobile: () -> Unit
) {
    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                UiSplashEvents.ChannelsNavigationEvent -> onNavigateToChannels()
                UiSplashEvents.LoginNavigationEvent -> onNavigateToLogin()
                UiSplashEvents.ChannelsMobileNavigationEvent -> onNavigateToChannelsMobile()
                UiSplashEvents.LoginMobileNavigationEvent -> onNavigateToLoginMobile()
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
        AndroidView(
            modifier = modifier
                .fillMaxSize(),
            factory = { context ->
                ImageView(context).apply {
                    setImageResource(R.drawable.splash_background)
                    scaleType = ImageView.ScaleType.FIT_XY
                }
            },
        )
        /*Image(
            modifier = modifier
                .fillMaxSize(),
            painter = painterResource(id = R.drawable.splash_background),
            contentScale = ContentScale.FillBounds,
            contentDescription = "",
        )*/
        /*Image(
            modifier = modifier
                .align(Alignment.Center)
                .fillMaxWidth(0.85f),
            painter = painterResource(id = R.drawable.logo62),
            contentScale = ContentScale.FillWidth,
            contentDescription = "",
        )*/
    }
}

@Preview(device = Devices.AUTOMOTIVE_1024p, widthDp = 1280, heightDp = 720)
@Composable
private fun Preview() {
    Tele_TvTheme {
        SplashScreenScreen()
    }
}