package com.edurda77.impuls.tele_tv

import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.edurda77.impuls.tele_tv.domain.utils.DOWNLOAD_URL
import com.edurda77.impuls.tele_tv.domain.utils.IS_SCREEN_ON
import com.edurda77.impuls.tele_tv.navigation.NavController
import com.edurda77.impuls.tele_tv.resources.model.NavigationRoute
import com.edurda77.impuls.tele_tv.resources.theme.Tele_TvTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val screenStateFilter = IntentFilter()
        screenStateFilter.addAction(Intent.ACTION_USER_PRESENT)
        screenStateFilter.addAction(Intent.ACTION_SCREEN_ON)
        registerReceiver(UnlockReceiver(), screenStateFilter)
        val isScreenOn = intent.getBooleanExtra(IS_SCREEN_ON, false)

        enableEdgeToEdge()
        setContent {
            Tele_TvTheme {
                NavController(
                    startDestination = if (isScreenOn) NavigationRoute.Channels(DOWNLOAD_URL) else NavigationRoute.Splash
                )
            }
        }
    }
}