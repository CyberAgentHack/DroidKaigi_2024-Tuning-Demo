package com.example.album.ui.favorite

import com.example.album.model.Album
import com.example.album.model.Favorite

sealed interface FavoriteUiState {
  val host: String
  val id: String
  val accessCode: String

  data class Empty(
    override val host: String,
    override val id: String,
    override val accessCode: String
  ) : FavoriteUiState

  data class Error(
    override val host: String,
    override val id: String,
    override val accessCode: String
  ) : FavoriteUiState

  data class Success(
    override val host: String,
    override val id: String,
    override val accessCode: String,
    val favorite: Favorite,
    val noticeMessage: String? = null,
    val isReloading: Boolean = false,
  ) : FavoriteUiState
}
