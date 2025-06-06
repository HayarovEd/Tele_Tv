package com.edurda77.impuls.tele_tv.di

import com.edurda77.impuls.tele_tv.login.LoginScreenViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val viewModelModule = module {
    viewModelOf(::LoginScreenViewModel)
}
