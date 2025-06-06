package com.edurda77.impuls.tele_tv

import android.app.Application
import com.edurda77.impuls.tele_tv.di.baseModule
import com.edurda77.impuls.tele_tv.di.repoModule
import com.edurda77.impuls.tele_tv.di.viewModelModule
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