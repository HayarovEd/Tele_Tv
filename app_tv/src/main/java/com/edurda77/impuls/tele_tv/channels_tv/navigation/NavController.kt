package com.edurda77.impuls.tele_tv.channels_tv.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.edurda77.impuls.tele_tv.channels.ChannelsScreenRoot
import com.edurda77.impuls.tele_tv.login.LoginScreenRoot
import com.edurda77.impuls.tele_tv.player.PlayerScreenRoot
import com.edurda77.impuls.tele_tv.resources.model.NavigationRoute

@Composable
fun NavController(
    startDestination: NavigationRoute,
) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = startDestination) {

        composable<NavigationRoute.Login> {
            LoginScreenRoot(
                onNavigateToChannels = {
                    navController.navigate(NavigationRoute.Channels)
                }
            )
        }
        composable<NavigationRoute.Splash> {

        }
        composable<NavigationRoute.Player> {
            PlayerScreenRoot(
                onNavigateToChannels = {
                    navController.navigate(NavigationRoute.Channels)
                }
            )
        }
        composable<NavigationRoute.Channels> {
            ChannelsScreenRoot(
                onNavigateTopPlayer = {
                    navController.navigate(NavigationRoute.Player)
                }
            )
        }
    }
}