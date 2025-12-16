package com.edurda77.impuls.tele_tv.channels_tv

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.edurda77.base_module.navigation.NavController
import com.edurda77.impuls.tele_tv.domain.utils.DOWNLOAD_TV_URL
import com.edurda77.impuls.tele_tv.resources.model.NavigationRoute
import com.edurda77.impuls.tele_tv.resources.model.TypeFactory
import com.edurda77.impuls.tele_tv.resources.theme.Tele_TvTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val deviceInfo = getDeviceInfo()

        // Вывод в Logcat
        Log.d("DeviceInfo TELE TV", "Бренд: ${deviceInfo.brand}")
        Log.d("DeviceInfo TELE TV", "Модель: ${deviceInfo.model}")
        Log.d("DeviceInfo TELE TV", "Производитель: ${deviceInfo.manufacturer}")

        setContent {
            Tele_TvTheme {
                NavController(
                    isTv = true,
                    startDestination = NavigationRoute.Splash(false),
                    downloadUrl = DOWNLOAD_TV_URL,
                    typeFactory = TypeFactory.TELE
                )
            }
        }
    }

    data class DeviceInfo(
        val brand: String,
        val model: String,
        val manufacturer: String
    )

    private fun getDeviceInfo(): DeviceInfo {
        return DeviceInfo(
            brand = Build.BRAND,
            model = Build.MODEL,
            manufacturer = Build.MANUFACTURER
        )
    }
}