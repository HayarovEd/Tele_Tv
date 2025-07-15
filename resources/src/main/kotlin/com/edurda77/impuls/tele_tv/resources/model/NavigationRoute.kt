package com.edurda77.impuls.tele_tv.resources.model
import kotlinx.serialization.Serializable

sealed class NavigationRoute {
    @Serializable
    data object Login : NavigationRoute()


    @Serializable
    data object Splash : NavigationRoute()

    @Serializable
    data class Player(
        val channelId: String
    ) : NavigationRoute()

    @Serializable
    data class Channels(
        val downloadUrl: String
    ) : NavigationRoute()


}