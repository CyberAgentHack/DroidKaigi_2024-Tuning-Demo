package com.example.album.ui.top

import com.example.album.model.Album
import com.example.album.model.Event

data class TopViewModelState(
  val album: Album? = null,
  val isChecking: Boolean = false,
  val isError: Boolean = false,
  val host: String? = null,
  val id: String? = null,
  val accessCode: String? = null,
  val eventList: List<Event> = emptyList()
) {

  companion object {
    val INITIAL = TopViewModelState()
  }

  fun toUiState(): TopUiState {
    if (isChecking) {
      return TopUiState.Checking
    }
    if (isError) {
      return TopUiState.Error
    }
    if (album != null) {
      return TopUiState.Checked(
        host = host!!,
        id = id!!,
        accessCode = accessCode!!
      )
    }
    return TopUiState.Idle(eventList)
  }
}
