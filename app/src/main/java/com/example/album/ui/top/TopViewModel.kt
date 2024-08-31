package com.example.album.ui.top

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.album.infra.repository.AlbumRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class TopViewModel @Inject constructor(
  private val albumRepository: AlbumRepository
) : ViewModel() {

  private val viewModelState: MutableStateFlow<TopViewModelState> = MutableStateFlow(TopViewModelState.INITIAL)

  val uiState: StateFlow<TopUiState> = viewModelState
    .map(TopViewModelState::toUiState)
    .stateIn(
      viewModelScope,
      SharingStarted.Eagerly,
      viewModelState.value.toUiState()
    )

  fun checkAlbumExistence(
    host: String,
    id: String,
    accessCode: String
  ) {
    viewModelState.update {
      it.copy(
        isChecking = true
      )
    }

    viewModelScope.launch {
      runCatching {
        albumRepository.loadAlbum(host, id, accessCode)
      }
        .onSuccess { result ->
          viewModelState.update {
            it.copy(
              album = result,
              isChecking = false,
              host = host,
              id = id,
              accessCode = accessCode
            )
          }
        }
        .onFailure { e ->
          Timber.e(e)
          viewModelState.update {
            it.copy(
              isChecking = false,
              isError = true
            )
          }
        }
    }
  }

  fun clearCheckState() {
    viewModelState.update {
      it.copy(
        album = null,
        isError = false
      )
    }
  }
}
