package com.example.album.ui.top

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.album.infra.repository.AlbumRepository
import com.example.album.infra.repository.EventRepository
import com.example.album.model.Event
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
  private val albumRepository: AlbumRepository,
  private val eventRepository: EventRepository
) : ViewModel() {

  private val viewModelState: MutableStateFlow<TopViewModelState> = MutableStateFlow(TopViewModelState.INITIAL)

  val uiState: StateFlow<TopUiState> = viewModelState
    .map(TopViewModelState::toUiState)
    .stateIn(
      viewModelScope,
      SharingStarted.Eagerly,
      viewModelState.value.toUiState()
    )

  fun loadSavedEvent() {
    viewModelScope.launch {
      runCatching {
        eventRepository.loadEvent()
      }
        .onSuccess { result ->
          viewModelState.update {
            it.copy(
              eventList = result
            )
          }
        }
        .onFailure { Timber.e(it) }
    }
  }

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

  fun saveEvent() {
    val host = viewModelState.value.host ?: return
    val id = viewModelState.value.id ?: return
    val name = viewModelState.value.album?.name ?: return
    val accessCode = viewModelState.value.accessCode ?: return
    val event = Event(
      host = host,
      id = id,
      name = name,
      accessCode = accessCode
    )

    viewModelScope.launch {
      runCatching { eventRepository.saveEvent(event) }
        .onSuccess { Timber.d("Success to save") }
        .onFailure { Timber.e(it) }
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
