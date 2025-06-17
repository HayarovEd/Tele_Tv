package com.edurda77.impuls.tele_tv.data.repository

import android.app.Application
import com.edurda77.impuls.tele_tv.domain.repository.ServoceRepository

class ServoceRepositoryImpl(
    private val application: Application
): ServoceRepository {

    override fun getVersionName(): Double? =
        application.packageManager.getPackageInfo(application.packageName, 0).versionName?.toDoubleOrNull()
}