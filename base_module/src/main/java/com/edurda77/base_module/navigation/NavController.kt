package com.edurda77.base_module.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.edurda77.impuls.tele_tv.channels.ChannelsScreenRoot
import com.edurda77.impuls.tele_tv.login.LoginScreenRoot
import com.edurda77.impuls.tele_tv.player.PlayerScreenRoot
import com.edurda77.impuls.tele_tv.resources.model.NavigationRoute
import com.edurda77.impuls.tele_tv.resources.model.TypeFactory
import com.edurda77.impuls.tele_tv.splash.SplashScreenRoot

@Composable
fun NavController(
    isTv: Boolean,
    typeFactory: TypeFactory,
    downloadUrl: String,
    startDestination: NavigationRoute,
) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = startDestination) {

        composable<NavigationRoute.Login> {
            LoginScreenRoot(
                typeFactory = typeFactory,
                onNavigateToChannels = {
                    navController.navigate(NavigationRoute.Channels(downloadUrl))
                }
            )
        }
        composable<NavigationRoute.Splash> {
            SplashScreenRoot(
                onNavigateToLogin = {
                    navController.navigate(NavigationRoute.Login)
                },
                onNavigateToChannels = {
                    navController.navigate(NavigationRoute.Channels(downloadUrl))
                },
                onNavigateToLoginMobile = {
                    navController.navigate(NavigationRoute.LoginMobile)
                },
                onNavigateToChannelsMobile = {
                    navController.navigate(NavigationRoute.ChannelsMobile(downloadUrl))
                },
                typeFactory = typeFactory
            )
        }
        composable<NavigationRoute.Player> {
            PlayerScreenRoot(
                isTv = isTv,
                onNavigateToChannels = {
                    navController.navigateUp()
                }
            )
        }
        composable<NavigationRoute.Channels> {
            ChannelsScreenRoot(
                typeFactory = typeFactory,
                onNavigateToPlayer = {
                    navController.navigate(NavigationRoute.Player(it))
                },
                onNavigateToLogin = {
                    navController.navigate(NavigationRoute.Login)
                }
            )
        }
    }
}