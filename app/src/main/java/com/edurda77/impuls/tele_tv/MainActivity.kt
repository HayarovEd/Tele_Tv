package com.edurda77.impuls.tele_tv

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.edurda77.impuls.tele_tv.login.LoginScreenRoot
import com.edurda77.impuls.tele_tv.resources.theme.Tele_TvTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Tele_TvTheme {
                LoginScreenRoot(
                    onNavigateToChannels = {}
                )
            }
        }
    }
}