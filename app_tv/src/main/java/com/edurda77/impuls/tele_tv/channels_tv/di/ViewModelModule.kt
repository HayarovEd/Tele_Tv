package com.edurda77.impuls.tele_tv.channels_tv.di

import com.edurda77.impuls.tele_tv.channels.ChannelsScreenViewModel
import com.edurda77.impuls.tele_tv.login.LoginScreenViewModel
import com.edurda77.impuls.tele_tv.player.PlayerScreenViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val viewModelModule = module {
    viewModelOf(::LoginScreenViewModel)
    viewModelOf(::PlayerScreenViewModel)
    viewModelOf(::ChannelsScreenViewModel)

}
