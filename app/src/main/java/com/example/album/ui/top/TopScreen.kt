package com.example.album.ui.top

import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.album.R
import com.example.album.model.Event
import com.example.album.ui.theme.AlbumTheme
import kotlinx.coroutines.launch

@Composable
fun TopScreen(
  viewModel: TopViewModel = hiltViewModel(),
  navigateToAlbum: ((String, String, String) -> Unit)? = null
) {
  val state by viewModel.uiState.collectAsStateWithLifecycle()
  val coroutineScope = rememberCoroutineScope()

  val snackbarHostState = remember { SnackbarHostState() }
  val onShowScankbar: (String) -> Unit = {
    coroutineScope.launch {
      viewModel.clearCheckState()
      snackbarHostState.showSnackbar(it)
    }
  }

  if (state is TopUiState.Checked) {
    val checkedState = state as TopUiState.Checked
    viewModel.saveEvent()
    viewModel.clearCheckState()
    navigateToAlbum?.invoke(
      Uri.encode(checkedState.host),
      Uri.encode(checkedState.id),
      Uri.encode(checkedState.accessCode)
    )
  }

  LifecycleEventEffect(event = Lifecycle.Event.ON_START) {
    viewModel.loadSavedEvent()
  }

  TopScaffold(
    state = state,
    onShowSnackbar = onShowScankbar,
    onClickLaunch = { host, id, accessCode ->
      viewModel.checkAlbumExistence(host, id, accessCode)
    },
    onClickSavedEvent = { event ->
      navigateToAlbum?.invoke(
        Uri.encode(event.host),
        Uri.encode(event.id),
        Uri.encode(event.accessCode)
      )
    },
    snackbarHostState = snackbarHostState
  )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopScaffold(
  state: TopUiState,
  snackbarHostState: SnackbarHostState,
  onClickLaunch: ((String, String, String) -> Unit)? = null,
  onClickSavedEvent: ((Event) -> Unit)? = null,
  onShowSnackbar: ((String) -> Unit)? = null
) {
  Scaffold(
    topBar = {
      TopAppBar(
        colors = topAppBarColors(
          containerColor = MaterialTheme.colorScheme.primaryContainer,
          titleContentColor = MaterialTheme.colorScheme.primary
        ),
        title = { Text(stringResource(R.string.label_setup)) },
      )
    },
    snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
  ) { innerPadding ->
    val eventList = (state as? TopUiState.Idle)?.eventList ?: emptyList()
    TopContent(
      modifier = Modifier.padding(innerPadding),
      eventList = eventList,
      onClickLaunch = onClickLaunch,
      onClickSavedEvent = onClickSavedEvent
    )

    when (state) {
      is TopUiState.Error -> {
        onShowSnackbar?.invoke(stringResource(R.string.message_failed_check_album))
      }
      else -> {}
    }
  }
}

@Composable
private fun TopContent(
  modifier: Modifier = Modifier,
  eventList: List<Event>,
  onClickLaunch: ((String, String, String) -> Unit)? = null,
  onClickSavedEvent: ((Event) -> Unit)? = null
) {
  // Default values for demonstration
  val host = remember {
    mutableStateOf("https://photospeedway.dokup.dev")
  }
  val id = remember {
    mutableStateOf("1")
  }
  val accessCode = remember {
    mutableStateOf("demo_access_code")
  }
  Surface {
    Column(
      modifier = modifier
        .fillMaxSize()
        .padding(16.dp)
    ) {
      Text(
        text = stringResource(R.string.label_host),
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold
      )
      Spacer(modifier = Modifier.height(4.dp))
      OutlinedTextField(
        value = host.value,
        onValueChange = { host.value = it },
        modifier = Modifier
          .fillMaxWidth()
      )
      Spacer(modifier = Modifier.height(12.dp))
      Text(
        text = stringResource(R.string.label_id),
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold
      )
      Spacer(modifier = Modifier.height(4.dp))
      OutlinedTextField(
        value = id.value,
        onValueChange = { id.value = it },
        modifier = Modifier
          .fillMaxWidth()
      )
      Spacer(modifier = Modifier.height(12.dp))
      Text(
        text = stringResource(R.string.label_access_code),
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold
      )
      Spacer(modifier = Modifier.height(4.dp))
      OutlinedTextField(
        value = accessCode.value,
        onValueChange = { accessCode.value = it },
        modifier = Modifier
          .fillMaxWidth()
      )
      Spacer(modifier = Modifier.height(24.dp))
      Button(
        onClick = { onClickLaunch?.invoke(host.value, id.value, accessCode.value) },
        modifier = Modifier.fillMaxWidth()
      ) {
        Text(
          text = stringResource(R.string.label_launch)
        )
      }
      if (eventList.isNotEmpty()) {
        Spacer(modifier = Modifier.height(24.dp))
        HorizontalDivider(modifier = Modifier.fillMaxWidth())
        LazyColumn(
          modifier = Modifier.fillMaxWidth()
        ) {
          items(
            items = eventList,
            key = {
              it.host + it.id
            }
          ) {
            SavedEvent(
              event = it,
              onClickSavedEvent = onClickSavedEvent
            )
          }
        }
      }
    }
  }
}

@Composable
private fun SavedEvent(
  event: Event,
  onClickSavedEvent: ((Event) -> Unit)? = null
) {
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .clickable { onClickSavedEvent?.invoke(event) }
  ) {
    Spacer(modifier = Modifier.height(8.dp))
    Text(
      text = event.host,
      fontSize = 12.sp
    )
    Text(
      text = event.id,
      fontSize = 12.sp
    )
    Text(
      text = event.name,
      fontSize = 12.sp
    )
    Spacer(modifier = Modifier.height(8.dp))
    HorizontalDivider(modifier = Modifier.fillMaxWidth())
  }
}

@Preview
@Composable
private fun PreviewTopContent() {
  AlbumTheme {
    TopScaffold(
      state = TopUiState.Idle(eventList = emptyList()),
      snackbarHostState = SnackbarHostState()
    )
  }
}
