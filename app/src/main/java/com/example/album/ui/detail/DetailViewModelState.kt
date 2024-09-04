package com.example.album.ui.detail

import com.example.album.model.ExifInfo
import com.example.album.model.Photo

data class DetailViewModelState(
  val photo: Photo? = null,
  val exifInfo: ExifInfo? = null,
  val noticeMessage: String? = null,
  val isLoading: Boolean = true,
  val isError: Boolean = false
) {

  companion object {
    val INITIAL = DetailViewModelState()
  }

  fun toUiState(): DetailUiState {
    if (isError) {
      return DetailUiState.Error
    }
    if (isLoading) {
      return DetailUiState.Loading
    }
    if (photo != null && exifInfo != null) {
      return DetailUiState.Success(photo, exifInfo, noticeMessage)
    }
    return DetailUiState.Loading
  }
}
