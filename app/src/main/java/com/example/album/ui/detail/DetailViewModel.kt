package com.example.album.ui.detail

import android.content.Context
import android.graphics.Bitmap
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.album.infra.repository.AlbumRepository
import com.example.album.infra.repository.ExifInfoRepository
import com.example.album.ui.album.AlbumUiState
import com.example.album.ui.album.AlbumViewModelState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
  private val albumRepository: AlbumRepository,
  private val exifInfoRepository: ExifInfoRepository,
  savedStateHandle: SavedStateHandle,
  @ApplicationContext private val context: Context
) : ViewModel() {

  private val host: String = checkNotNull(savedStateHandle["host"])
  private val id: String = checkNotNull(savedStateHandle["id"])
  private val accessCode: String = checkNotNull(savedStateHandle["access_code"])
  private val imageUrl: String = checkNotNull(savedStateHandle["image_url"])

  private val viewModelState: MutableStateFlow<DetailViewModelState> = MutableStateFlow(DetailViewModelState.INITIAL)

  val uiState: StateFlow<DetailUiState> = viewModelState
    .map(DetailViewModelState::toUiState)
    .stateIn(
      viewModelScope,
      SharingStarted.Eagerly,
      viewModelState.value.toUiState()
    )

  fun load() {
    viewModelState.update {
      it.copy(
        isLoading = true,
        isError = false
      )
    }
    viewModelScope.launch {
      val photo = runCatching {
        albumRepository.loadAlbum(host, id, accessCode).photoList.find { it.imageUrl == imageUrl }
      }.getOrNull()
      val exifInfo = runCatching {
        exifInfoRepository.getExifInfo(host, imageUrl)
      }.getOrNull()

      if (photo == null || exifInfo == null) {
        viewModelState.update {
          it.copy(
            isError = true
          )
        }
        return@launch
      }

      viewModelState.update {
        it.copy(
          photo = photo,
          exifInfo = exifInfo,
          isLoading = false
        )
      }
    }
  }

  fun clearNoticeState() {
    viewModelState.update {
      it.copy(
        isError = false,
        noticeMessage = null
      )
    }
  }

  fun savePhoto(
    bitmap: Bitmap,
    shotDateTime: Instant,
    fileName: String
  ) {
    runCatching {
      albumRepository.savePhoto(
        context = context,
        bitmap = bitmap,
        shotDateTime = shotDateTime,
        fileName = fileName
      )
    }
      .onSuccess {
        viewModelState.update {
          it.copy(
            noticeMessage = "Succeed to save file: $fileName"
          )
        }
      }
      .onFailure { e ->
        Timber.e(e)
        viewModelState.update {
          it.copy(
            noticeMessage = "Failed to save file: $fileName"
          )
        }
      }
  }
}
