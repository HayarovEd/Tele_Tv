package com.edurda77.impuls.tele_tv.channels_tv

import android.content.Context
import android.os.BatteryManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.PowerManager
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.edurda77.base_module.navigation.NavController
import com.edurda77.impuls.tele_tv.domain.utils.DOWNLOAD_TV_URL
import com.edurda77.impuls.tele_tv.resources.model.NavigationRoute
import com.edurda77.impuls.tele_tv.resources.model.TypeFactory
import com.edurda77.impuls.tele_tv.resources.theme.Tele_TvTheme
import com.edurda77.impuls.tele_tv.resources.uikit.KeepScreenOn

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        disableScreenSaver()
        setContent {
            Tele_TvTheme {
                KeepScreenOn()
                NavController(
                    isTv = true,
                    startDestination = NavigationRoute.Splash(false),
                    downloadUrl = DOWNLOAD_TV_URL,
                    typeFactory = TypeFactory.TELE
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    override fun onPause() {
        super.onPause()
        window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    override fun onUserInteraction() {
        super.onUserInteraction()
        // Сбрасываем таймер заставки при взаимодействии пользователя
        resetScreensaverTimer()
    }

    private fun resetScreensaverTimer() {
        window.decorView.apply {
            keepScreenOn = true
            // Сбрасываем флаг через 10 секунд для обновления
            postDelayed({
                keepScreenOn = false
                keepScreenOn = true
            }, 10000)
        }
    }


    private fun disableScreenSaver() {
        // Методы для разных версий Android TV
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Для Android 10+
            val stayOnWhilePluggedIn = (
                    BatteryManager.BATTERY_PLUGGED_AC or
                            BatteryManager.BATTERY_PLUGGED_USB or
                            BatteryManager.BATTERY_PLUGGED_WIRELESS
                    )

            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

            // Используем системный сервис
            val powerManager = getSystemService(POWER_SERVICE) as PowerManager
            val wakeLock = powerManager.newWakeLock(
                PowerManager.SCREEN_BRIGHT_WAKE_LOCK or
                        PowerManager.ACQUIRE_CAUSES_WAKEUP,
                "TVApp::DisableScreensaver"
            )
            wakeLock.acquire(10*60*1000L /*10 minutes*/)

            // Таймер для автоматического освобождения
            Handler(Looper.getMainLooper()).postDelayed({
                if (wakeLock.isHeld) {
                    wakeLock.release()
                }
            }, 300000) // 5 минут
        }
    }

    /*Бренд: BBK
    Модель: 50LEX_8158_UTS2C
    Производитель: Realtek*/
}