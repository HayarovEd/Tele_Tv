package com.edurda77.impuls.tele_tv.channels

sealed class UiChannelsEvents {
    data object PlayerNavigationEvent:UiChannelsEvents()
    data object LoginNavigationEvent:UiChannelsEvents()
}