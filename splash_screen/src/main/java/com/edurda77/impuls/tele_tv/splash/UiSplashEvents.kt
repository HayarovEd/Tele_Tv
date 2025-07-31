package com.edurda77.impuls.tele_tv.splash

sealed class UiSplashEvents {
    data object ChannelsNavigationEvent : UiSplashEvents()
    data object LoginNavigationEvent : UiSplashEvents()
    data object LoginMobileNavigationEvent : UiSplashEvents()
    data object ChannelsMobileNavigationEvent : UiSplashEvents()
}