package com.example.album.ui.favorite

import android.content.Context
import android.graphics.Bitmap
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.album.infra.repository.AlbumRepository
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
class FavoriteViewModel @Inject constructor(
  private val albumRepository: AlbumRepository,
  savedStateHandle: SavedStateHandle,
  @ApplicationContext private val context: Context
) : ViewModel() {

  private val host: String = checkNotNull(savedStateHandle["host"])
  private val id: String = checkNotNull(savedStateHandle["id"])
  private val accessCode: String = checkNotNull(savedStateHandle["access_code"])

  private val viewModelState: MutableStateFlow<FavoriteViewModelState> = MutableStateFlow(FavoriteViewModelState.INITIAL)

  val uiState: StateFlow<FavoriteUiState> = viewModelState
    .map(FavoriteViewModelState::toUiState)
    .stateIn(
      viewModelScope,
      SharingStarted.Eagerly,
      viewModelState.value.toUiState()
    )

  fun loadFavorite(isReloading: Boolean = false) {
    viewModelState.update {
      it.copy(
        isReloading = isReloading,
        host = host,
        id = id,
        accessCode = accessCode
      )
    }
    viewModelScope.launch {
      runCatching {
        albumRepository.loadFavorite(host, id, accessCode)
      }
        .onSuccess { result ->
          viewModelState.update {
            it.copy(
              favorite = result,
              isReloading = false
            )
          }
        }
        .onFailure { e ->
          Timber.e(e)
          viewModelState.update {
            it.copy(
              isError = true
            )
          }
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
    viewModelScope.launch {
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
}
