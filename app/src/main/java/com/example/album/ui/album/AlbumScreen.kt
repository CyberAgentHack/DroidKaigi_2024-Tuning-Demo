package com.example.album.ui.album

import android.graphics.Bitmap
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.SubcomposeAsyncImage
import com.example.album.R
import com.example.album.misc.formatDateTime
import com.example.album.model.Photo
import com.example.album.ui.theme.AlbumTheme
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant

@Composable
fun AlbumScreen(
  viewModel: AlbumViewModel = hiltViewModel(),
  navigateToDetail: ((String, String, String, String) -> Unit)? = null
) {
  val state by viewModel.uiState.collectAsStateWithLifecycle()
  val coroutineScope = rememberCoroutineScope()

  val snackbarHostState = remember { SnackbarHostState() }
  val onShowScankbar: (String) -> Unit = {
    coroutineScope.launch {
      viewModel.clearNoticeState()
      snackbarHostState.showSnackbar(it)
    }
  }

  LifecycleEventEffect(event = Lifecycle.Event.ON_START) {
    viewModel.loadAlbum()
  }

  AlbumScaffold(
    state = state,
    snackbarHostState = snackbarHostState,
    onShowSnackbar = onShowScankbar,
    onClickImage = {
      navigateToDetail?.invoke(
        Uri.encode(state.host),
        Uri.encode(state.id),
        Uri.encode(state.accessCode),
        Uri.encode(it)
      )
    },
    onClickSave = { bitmap, shotDateTime, fileName ->
      viewModel.savePhoto(bitmap, shotDateTime, fileName)
    },
    onRefresh = { viewModel.loadAlbum(isReloading = true) }
  )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AlbumScaffold(
  state: AlbumUiState,
  snackbarHostState: SnackbarHostState,
  onShowSnackbar: ((String) -> Unit)? = null,
  onClickImage: ((String) -> Unit)? = null,
  onClickSave: ((Bitmap, Instant, String) -> Unit)? = null,
  onRefresh: (() -> Unit)? = null
) {
  Scaffold(
    topBar = {
      TopAppBar(
        colors = topAppBarColors(
          containerColor = MaterialTheme.colorScheme.primaryContainer,
          titleContentColor = MaterialTheme.colorScheme.primary
        ),
        title = { Text(stringResource(R.string.label_album)) },
      )
    },
    snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
  ) { innerPadding ->
    if (state is AlbumUiState.Success) {
      AlbumContent(
        modifier = Modifier.padding(innerPadding),
        photoList = state.album.photoList,
        isReloading = state.isReloading,
        onClickImage = onClickImage,
        onClickSave = onClickSave,
        onRefresh = onRefresh
      )

      if (state.noticeMessage != null) {
        onShowSnackbar?.invoke(state.noticeMessage)
      }
    }

    when (state) {
      is AlbumUiState.Error -> {
        onShowSnackbar?.invoke("Failed to load album")
      }
      else -> {}
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AlbumContent(
  modifier: Modifier = Modifier,
  photoList: List<Photo>,
  isReloading: Boolean,
  onClickImage: ((String) -> Unit)? = null,
  onClickSave: ((Bitmap, Instant, String) -> Unit)? = null,
  onRefresh: (() -> Unit)? = null
) {
  Surface {
    PullToRefreshBox(
      modifier = modifier,
      isRefreshing = isReloading,
      onRefresh = { onRefresh?.invoke() }
    ) {
      LazyColumn(
        modifier = Modifier
          .fillMaxSize()
          .padding(16.dp)
      ) {
        item {
          for (photo in photoList) {
            PhotoItem(
              photo = photo,
              onClickImage = onClickImage,
              onClickSave = onClickSave
            )
          }
        }
      }
    }
  }
}

@Composable
private fun PhotoItem(
  photo: Photo,
  onClickImage: ((String) -> Unit)? = null,
  onClickSave: ((Bitmap, Instant, String) -> Unit)? = null
) {
  val bitmap = remember { mutableStateOf<Bitmap?>(null) }
  Column(
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    SubcomposeAsyncImage(
      modifier = Modifier
        .fillMaxWidth()
        .clickable { onClickImage?.invoke(photo.imageUrl) },
      model = photo.imageUrl,
      contentDescription = null,
      onSuccess = {
        bitmap.value = it.result.drawable.toBitmap()
      }
    )
    Spacer(modifier = Modifier.height(4.dp))
    Text(
      text = photo.fileName,
      fontSize = 14.sp
    )
    Text(
      text = photo.shotDateTime.formatDateTime(),
      fontSize = 14.sp
    )
    Spacer(modifier = Modifier.height(4.dp))
    Button(
      onClick = {
        val savingBitmap = bitmap.value
        if (savingBitmap != null) {
          onClickSave?.invoke(savingBitmap, photo.shotDateTime, photo.fileName)
        }
      },
      modifier = Modifier.fillMaxWidth()
    ) {
      Text(
        text = stringResource(R.string.label_save)
      )
    }
    Spacer(modifier = Modifier.height(16.dp))
  }
}

@Preview
@Composable
private fun PreviewAlbumContent() {
  AlbumTheme {
    AlbumScaffold(
      state = AlbumUiState.Empty(
        host = "https://example.com",
        id = "1",
        accessCode = "access_code"
      ),
      snackbarHostState = SnackbarHostState()
    )
  }
}
