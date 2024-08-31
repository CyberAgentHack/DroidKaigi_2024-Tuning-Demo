package com.example.album.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.album.ui.album.AlbumScreen
import com.example.album.ui.top.TopScreen

@Composable
fun AppNavHost(modifier: Modifier = Modifier) {
  val navController = rememberNavController()

  NavHost(
    navController = navController,
    startDestination = AppNavigation.Top.route,
    modifier = modifier
  ) {
    composable(AppNavigation.Top.route) {
      TopScreen(
        navigateToAlbum = { host, id, accessCode ->
          navController.navigate(
            AppNavigation.Album.createRoute(
              host = host,
              id = id,
              accessCode = accessCode
            )
          ) {
            launchSingleTop = true
          }
        }
      )
    }

    composable(
      route = AppNavigation.Album.route,
      arguments = listOf(
        navArgument(AppNavigation.Album.HOST_ARG) { type = NavType.StringType },
        navArgument(AppNavigation.Album.ID_ARG) { type = NavType.StringType },
        navArgument(AppNavigation.Album.ACCESS_CODE_ARG) { type = NavType.StringType }
      )
    ) {
      AlbumScreen()
    }

    composable(
      route = AppNavigation.Detail.route,
      arguments = listOf(
        navArgument(AppNavigation.Detail.URL_ARG) { type = NavType.StringType }
      )
    ) {

    }
  }
}
