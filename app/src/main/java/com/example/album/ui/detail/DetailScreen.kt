package com.example.album.ui.detail

import android.graphics.Bitmap
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
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
import com.example.album.model.ExifInfo
import com.example.album.model.Photo
import com.example.album.ui.theme.AlbumTheme
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

@Composable
fun DetailScreen(
  viewModel: DetailViewModel = hiltViewModel(),
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
    viewModel.load()
  }

  DetailScaffold(
    state = state,
    snackbarHostState = snackbarHostState,
    onShowSnackbar = onShowScankbar,
    onClickSave = { bitmap, shotDateTime, fileName ->
      viewModel.savePhoto(bitmap, shotDateTime, fileName)
    }
  )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DetailScaffold(
  state: DetailUiState,
  snackbarHostState: SnackbarHostState,
  onShowSnackbar: ((String) -> Unit)? = null,
  onClickSave: ((Bitmap, Instant, String) -> Unit)? = null
) {
  Scaffold(
    snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
  ) { innerPadding ->
    if (state is DetailUiState.Success) {
      DetailContent(
        modifier = Modifier.padding(innerPadding),
        photo = state.photo,
        exifInfo = state.exifInfo,
        onClickSave = onClickSave
      )

      if (state.noticeMessage != null) {
        onShowSnackbar?.invoke(state.noticeMessage)
      }
    }

    when (state) {
      is DetailUiState.Error -> {
        onShowSnackbar?.invoke("Failed to load photo")
      }
      else -> {}
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DetailContent(
  modifier: Modifier = Modifier,
  photo: Photo,
  exifInfo: ExifInfo,
  onClickSave: ((Bitmap, Instant, String) -> Unit)? = null
) {
  val bitmap = remember { mutableStateOf<Bitmap?>(null) }
  Box(
    modifier = Modifier
      .background(color = Color.DarkGray)
      .fillMaxSize(),
    contentAlignment = Alignment.BottomCenter
  ) {
    SubcomposeAsyncImage(
      modifier = Modifier.fillMaxSize(),
      model = photo.imageUrl,
      contentDescription = null,
      onSuccess = {
        bitmap.value = it.result.drawable.toBitmap()
      }
    )
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .background(Color(0x33000000))
        .padding(24.dp)
    ) {
      Text(
        text = photo.fileName,
        fontSize = 16.sp,
        color = Color.White,
        fontWeight = FontWeight.Bold
      )
      Text(
        text = photo.shotDateTime.formatDateTime(),
        fontSize = 14.sp,
        color = Color.White,
      )
      Spacer(modifier = Modifier.height(8.dp))
      Text(
        text = exifInfo.camera,
        fontSize = 14.sp,
        color = Color.White,
      )
      Text(
        text = exifInfo.lens,
        fontSize = 14.sp,
        color = Color.White,
      )
      Row(
        modifier = Modifier.fillMaxWidth()
      ) {
        Text(
          text = exifInfo.labeledShutterSpeed,
          fontSize = 14.sp,
          color = Color.White,
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = exifInfo.labeledFocalLength,
          fontSize = 14.sp,
          color = Color.White,
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = exifInfo.labeledAperture,
          fontSize = 14.sp,
          color = Color.White,
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = exifInfo.labeledIso,
          fontSize = 14.sp,
          color = Color.White,
        )
      }
      Spacer(modifier = Modifier.height(8.dp))
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
    }
  }
}

@Preview
@Composable
private fun PreviewDetailScaffold() {
  AlbumTheme {
    DetailScaffold(
      state = DetailUiState.Loading,
      snackbarHostState = SnackbarHostState()
    )
  }
}

@Preview
@Composable
private fun PreviewDetailContent() {
  val photo = Photo(
    imageUrl = "https://e10dokup.pecori.jp/demo/images/DSC07994s.jpg",
    fileName = "DSC12345.JPG",
    shotDateTime = Clock.System.now()
  )

  val exifInfo = ExifInfo(
    camera = "SONY ILCE-9M2",
    lens = "FE 200-600mm F5.6-6.3 G OSS",
    shutterSpeed = "1/500",
    focalLength = "600.0",
    aperture = "6.3",
    iso = "3200"
  )

  AlbumTheme {
    Surface(
      modifier = Modifier
        .background(color = Color.DarkGray)
    ) {
      DetailContent(
        photo = photo,
        exifInfo = exifInfo
      )
    }
  }
}
