package com.edurda77.impuls.tele_tv.data.repository

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.os.PowerManager
import com.edurda77.impuls.tele_tv.domain.repository.ServiceRepository

class ServiceRepositoryImpl(
    private val application: Application
): ServiceRepository {

    private var wakeLock: PowerManager.WakeLock? = null

    override fun getVersionName(): Double? {
       // val a = application.externalCacheDir?.absolutePath
        return application.packageManager.getPackageInfo(
            application.packageName,
            0
        ).versionName?.toDoubleOrNull()
    }

    @SuppressLint("Wakelock")
    override fun setWakeLock() {
        val powerManager = application.getSystemService(Context.POWER_SERVICE) as PowerManager
        wakeLock = powerManager.newWakeLock(
            PowerManager.PARTIAL_WAKE_LOCK or
                    PowerManager.ACQUIRE_CAUSES_WAKEUP or
                    PowerManager.ON_AFTER_RELEASE,
            "YourApp::PlayerWakeLock"
        )
        wakeLock?.acquire(10 * 60 * 1000L)
    }

    override fun releaseWakeLock() {
        wakeLock?.let {
            if (it.isHeld) {
                it.release()
            }
        }
        wakeLock = null
    }
}