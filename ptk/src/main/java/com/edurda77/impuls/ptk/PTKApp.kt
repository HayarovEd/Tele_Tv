package com.edurda77.impuls.ptk

import android.app.Application
import com.edurda77.base_module.di.baseModule
import com.edurda77.base_module.di.repoModule
import com.edurda77.base_module.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class PTKApp: Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@PTKApp)
            modules(
                baseModule, viewModelModule, repoModule
            )
        }
    }
}