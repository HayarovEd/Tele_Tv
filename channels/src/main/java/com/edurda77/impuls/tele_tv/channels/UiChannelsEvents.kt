package com.edurda77.impuls.tele_tv.channels

sealed class UiChannelsEvents {
    class PlayerNavigationEvent(val channelId: String):UiChannelsEvents()
    data object LoginNavigationEvent:UiChannelsEvents()
}