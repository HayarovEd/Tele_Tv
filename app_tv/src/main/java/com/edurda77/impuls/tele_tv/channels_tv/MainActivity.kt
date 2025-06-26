package com.edurda77.impuls.tele_tv.channels_tv

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.edurda77.impuls.tele_tv.channels_tv.navigation.NavController
import com.edurda77.impuls.tele_tv.resources.model.NavigationRoute
import com.edurda77.impuls.tele_tv.resources.theme.Tele_TvTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       // enableEdgeToEdge()
        setContent {
            Tele_TvTheme {
                NavController(
                    startDestination = NavigationRoute.Splash
                )
            }
        }
    }
}