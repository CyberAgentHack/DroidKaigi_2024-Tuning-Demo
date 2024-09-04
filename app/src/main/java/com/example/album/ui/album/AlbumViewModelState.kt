package com.example.album.ui.album

import com.example.album.model.Album

data class AlbumViewModelState(
  val album: Album? = null,
  val noticeMessage: String? = null,
  val isError: Boolean = false,
  val isReloading: Boolean = false,
  val host: String = "",
  val id: String = "",
  val accessCode: String = ""
) {

  companion object {
    val INITIAL = AlbumViewModelState()
  }

  fun toUiState(): AlbumUiState {
    if (isError) {
      return AlbumUiState.Error(
        host = host,
        id = id,
        accessCode = accessCode
      )
    }
    if (album != null) {
      return AlbumUiState.Success(
        host = host,
        id = id,
        accessCode = accessCode,
        album = album,
        noticeMessage = noticeMessage,
        isReloading = isReloading
      )
    }
    return AlbumUiState.Empty(
      host = host,
      id = id,
      accessCode = accessCode
    )
  }
}
