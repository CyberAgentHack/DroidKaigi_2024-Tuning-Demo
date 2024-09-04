package com.example.album.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Home
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.album.App

internal sealed interface AppNavigation {
  val route: String

  data object Top : AppNavigation {
    override val route: String
      get() = "top"
  }

  data object Album : AppNavigation {
    const val HOST_ARG = "host"
    const val ID_ARG = "id"
    const val ACCESS_CODE_ARG = "access_code"

    override val route: String = "album/{$HOST_ARG}/{$ID_ARG}/{$ACCESS_CODE_ARG}"

    fun createRoute(
      host: String,
      id: String,
      accessCode: String
    ): String {
      return "album/$host/$id/$accessCode"
    }
  }

  data object Detail : AppNavigation {
    const val HOST_ARG = "host"
    const val ID_ARG = "id"
    const val ACCESS_CODE_ARG = "access_code"
    const val URL_ARG = "image_url"

    override val route: String = "detail/{$HOST_ARG}/{$ID_ARG}/{$ACCESS_CODE_ARG}/{$URL_ARG}"

    fun createRoute(
    host: String,
    id: String,
    accessCode: String,
    imageUrl: String
    ): String {
      return "detail/$host/$id/$accessCode/$imageUrl"
    }
  }
}
