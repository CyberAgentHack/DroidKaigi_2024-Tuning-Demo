package com.example.album.ui.favorite

import com.example.album.model.Favorite

data class FavoriteViewModelState(
  val favorite: Favorite? = null,
  val noticeMessage: String? = null,
  val isError: Boolean = false,
  val isReloading: Boolean = false,
  val host: String = "",
  val id: String = "",
  val accessCode: String = ""
) {

  companion object {
    val INITIAL = FavoriteViewModelState()
  }

  fun toUiState(): FavoriteUiState {
    if (isError) {
      return FavoriteUiState.Error(
        host = host,
        id = id,
        accessCode = accessCode
      )
    }
    if (favorite != null) {
      return FavoriteUiState.Success(
        host = host,
        id = id,
        accessCode = accessCode,
        favorite = favorite,
        noticeMessage = noticeMessage,
        isReloading = isReloading
      )
    }
    return FavoriteUiState.Empty(
      host = host,
      id = id,
      accessCode = accessCode
    )
  }
}
