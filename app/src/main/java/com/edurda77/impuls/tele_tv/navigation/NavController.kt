package com.edurda77.impuls.tele_tv.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.edurda77.impuls.tele_tv.channels.ChannelsScreenRoot
import com.edurda77.impuls.tele_tv.domain.utils.DOWNLOAD_URL
import com.edurda77.impuls.tele_tv.login.LoginScreenRoot
import com.edurda77.impuls.tele_tv.player.PlayerScreenRoot
import com.edurda77.impuls.tele_tv.resources.model.NavigationRoute
import com.edurda77.impuls.tele_tv.splash.SplashScreenRoot

@Composable
fun NavController(
    startDestination: NavigationRoute,
) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = startDestination) {

        composable<NavigationRoute.Login> {
            LoginScreenRoot(
                onNavigateToChannels = {
                    navController.navigate(NavigationRoute.Channels(DOWNLOAD_URL))
                }
            )
        }
        composable<NavigationRoute.Splash> {
            SplashScreenRoot(
                onNavigateToLogin = {
                    navController.navigate(NavigationRoute.Login)
                },
                onNavigateToChannels = {
                    navController.navigate(NavigationRoute.Channels(DOWNLOAD_URL))
                }
            )
        }
        composable<NavigationRoute.Player> {
            PlayerScreenRoot(
                onNavigateToChannels = {
                    navController.navigateUp()
                }
            )
        }
        composable<NavigationRoute.Channels> {
            ChannelsScreenRoot(
                onNavigateTopPlayer = {
                    navController.navigate(NavigationRoute.Player)
                },
                onNavigateToLogin = {
                    navController.navigate(NavigationRoute.Login)
                }
            )
        }
    }
}