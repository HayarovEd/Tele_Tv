package com.edurda77.impuls.tele_tv.resources.model
import kotlinx.serialization.Serializable

sealed class NavigationRoute {
    @Serializable
    data object Login : NavigationRoute()


    @Serializable
    data class Splash(
        val isMobile: Boolean
    ) : NavigationRoute()

    @Serializable
    data class Player(
        val channelId: String
    ) : NavigationRoute()

    @Serializable
    data class Channels(
        val downloadUrl: String
    ) : NavigationRoute()

    @Serializable
    data object LoginMobile : NavigationRoute()

    @Serializable
    data class PlayerMobile(
        val channelId: String
    ) : NavigationRoute()

    @Serializable
    data class ChannelsMobile(
        val downloadUrl: String
    ) : NavigationRoute()
}