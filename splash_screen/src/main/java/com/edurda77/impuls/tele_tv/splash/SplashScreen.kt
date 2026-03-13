package com.edurda77.impuls.tele_tv.splash

import android.widget.ImageView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.edurda77.impuls.tele_tv.resources.R
import com.edurda77.impuls.tele_tv.resources.model.TypeFactory
import com.edurda77.impuls.tele_tv.resources.theme.Tele_TvTheme
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.compose.koinViewModel

@Composable
fun SplashScreenRoot(
    typeFactory: TypeFactory,
    viewModel: SplashScreenViewModel = koinViewModel(),
    onNavigateToChannels: () -> Unit,
    onNavigateToChannelsMobile: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onNavigateToLoginMobile: () -> Unit
) {
    val context = LocalContext.current
    val version =
        remember { context.packageManager.getPackageInfo(context.packageName, 0).versionName ?: "" }

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

    SplashScreenScreen(
        typeFactory = typeFactory,
        version = version
    )
}

@Composable
private fun SplashScreenScreen(
    modifier: Modifier = Modifier,
    typeFactory: TypeFactory,
    version: String,
) {
    Box(modifier = modifier.fillMaxSize()) {
        AndroidView(
            modifier = modifier
                .fillMaxSize(),
            factory = { context ->
                ImageView(context).apply {
                    when (typeFactory) {
                        TypeFactory.TELE -> setImageResource(R.drawable.tele_splash_background)
                        TypeFactory.PTK -> setImageResource(R.drawable.ptk_splash_background)
                    }
                    scaleType = ImageView.ScaleType.FIT_XY
                }
            },
        )
        Text(
            modifier = modifier
                .align(
                    alignment = Alignment.BottomCenter,
                )
                .padding(bottom = 20.dp),
            text = "${stringResource(R.string.current_version)}: $version",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground,
        )
    }
}

@Preview(device = Devices.AUTOMOTIVE_1024p, widthDp = 1280, heightDp = 720)
@Composable
private fun PreviewTele() {
    Tele_TvTheme {
        SplashScreenScreen(
            typeFactory = TypeFactory.TELE,
            version = "1.0",
        )
    }
}

@Preview(device = Devices.AUTOMOTIVE_1024p, widthDp = 1280, heightDp = 720)
@Composable
private fun PreviewPTK() {
    Tele_TvTheme {
        SplashScreenScreen(
            typeFactory = TypeFactory.PTK,
            version = "1.0",
        )
    }
}