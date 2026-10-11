package com.edurda77.ptk_tv

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.edurda77.base_module.navigation.NavController
import com.edurda77.impuls.tele_tv.domain.utils.DOWNLOAD_PTK_TV_URL
import com.edurda77.impuls.tele_tv.resources.model.NavigationRoute
import com.edurda77.impuls.tele_tv.resources.model.TypeFactory
import com.edurda77.impuls.tele_tv.resources.theme.Tele_TvTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Tele_TvTheme {
                NavController(
                    isTv = true,
                    startDestination = NavigationRoute.Splash(false),
                    downloadUrl = DOWNLOAD_PTK_TV_URL,
                    typeFactory = TypeFactory.PTK
                )
            }
        }
    }
}