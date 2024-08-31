package com.example.album.ui.album

import com.example.album.model.Album

data class AlbumViewModelState(
  val album: Album? = null,
  val noticeMessage: String? = null,
  val isError: Boolean = false,
  val isReloading: Boolean = false
) {

  companion object {
    val INITIAL = AlbumViewModelState()
  }

  fun toUiState(): AlbumUiState {
    if (isError) {
      return AlbumUiState.Error
    }
    if (album != null) {
      return AlbumUiState.Success(
        album = album,
        noticeMessage = noticeMessage,
        isReloading = isReloading
      )
    }
    return AlbumUiState.Empty
  }
}
