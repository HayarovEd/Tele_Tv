package com.edurda77.impuls.tele_tv.channels_tv

import android.app.Application
import com.edurda77.base_module.di.baseModule
import com.edurda77.base_module.di.repoModule
import com.edurda77.base_module.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class TeleApp: Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@TeleApp)
            modules(
                baseModule, viewModelModule, repoModule
            )
        }
    }
}