package com.example.album.ui.top

import com.example.album.model.Album

sealed interface TopUiState {
  data object Idle : TopUiState

  data object Checking : TopUiState

  data object Error : TopUiState

  data class Checked(
    val host: String,
    val id: String,
    val accessCode: String
  ) : TopUiState
}
