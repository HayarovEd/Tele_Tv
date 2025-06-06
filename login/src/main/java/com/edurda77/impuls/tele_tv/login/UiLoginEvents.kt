package com.edurda77.impuls.tele_tv.login

sealed class UiLoginEvents {
    data object ChannelsNavigationEvent : UiLoginEvents()
}