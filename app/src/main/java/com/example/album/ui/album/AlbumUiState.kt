package com.example.album.ui.album

import com.example.album.model.Album

sealed interface AlbumUiState {
  data object Empty : AlbumUiState

  data object Error : AlbumUiState

  data class Success(
    val album: Album,
    val noticeMessage: String? = null,
    val isReloading: Boolean = false
  ) : AlbumUiState
}
