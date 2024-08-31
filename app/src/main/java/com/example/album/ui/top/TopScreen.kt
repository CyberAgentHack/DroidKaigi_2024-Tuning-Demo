package com.example.album.ui.top

import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.album.R
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
    viewModel.clearCheckState()
    navigateToAlbum?.invoke(
      Uri.encode(checkedState.host),
      Uri.encode(checkedState.id),
      Uri.encode(checkedState.accessCode)
    )
  }

  TopScaffold(
    state = state,
    onShowSnackbar = onShowScankbar,
    onClickLaunch = { host, id, accessCode ->
      viewModel.checkAlbumExistence(host, id, accessCode)
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
    TopContent(
      modifier = Modifier.padding(innerPadding),
      onClickLaunch = onClickLaunch
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
  onClickLaunch: ((String, String, String) -> Unit)? = null
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
    }
  }
}

@Preview
@Composable
private fun PreviewTopContent() {
  AlbumTheme {
    TopScaffold(
      state = TopUiState.Idle,
      snackbarHostState = SnackbarHostState()
    )
  }
}
