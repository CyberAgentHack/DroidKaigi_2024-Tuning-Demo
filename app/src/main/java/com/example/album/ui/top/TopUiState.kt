package com.example.album.ui.top

import com.example.album.model.Event

sealed interface TopUiState {
  data class Idle(
    val eventList: List<Event>
  ) : TopUiState

  data object Checking : TopUiState

  data object Error : TopUiState

  data class Checked(
    val host: String,
    val id: String,
    val accessCode: String
  ) : TopUiState
}
