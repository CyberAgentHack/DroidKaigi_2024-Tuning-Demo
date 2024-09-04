package com.example.album.ui.album

import com.example.album.model.Album

sealed interface AlbumUiState {
  val host: String
  val id: String
  val accessCode: String

  data class Empty(
    override val host: String,
    override val id: String,
    override val accessCode: String
  ) : AlbumUiState

  data class Error(
    override val host: String,
    override val id: String,
    override val accessCode: String
  ) : AlbumUiState

  data class Success(
    override val host: String,
    override val id: String,
    override val accessCode: String,
    val album: Album,
    val noticeMessage: String? = null,
    val isReloading: Boolean = false,
  ) : AlbumUiState
}
