package com.example.album.ui.detail

import com.example.album.model.ExifInfo
import com.example.album.model.Photo

sealed interface DetailUiState {
  data object Loading : DetailUiState

  data object Error : DetailUiState

  data class Success(
    val photo: Photo,
    val exifInfo: ExifInfo,
    val noticeMessage: String?
  ) : DetailUiState
}
