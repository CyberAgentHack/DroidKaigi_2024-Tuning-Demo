package com.example.album.infra.datasource.file

import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import kotlinx.datetime.Instant
import java.io.File
import java.net.URL
import javax.inject.Inject

class FileSaver @Inject constructor() {
  companion object {
    private const val DIRECTORY = "photospeedway"
  }

  fun saveFile(
    context: Context,
    bitmap: Bitmap,
    fileName: String,
    dateTime: Instant
  ): Boolean {
    val contentResolver = context.contentResolver
    val values = ContentValues().apply {
      put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
      put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
      put(
        MediaStore.Images.Media.DATE_MODIFIED,
        dateTime.epochSeconds
      )
    }

    val contentUri = getContentUri(
      resolver = contentResolver,
      values = values,
      fileName = fileName
    ) ?: return false

    val isSuccess = contentResolver.openOutputStream(contentUri, "w")?.use { stream ->
      bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
    } ?: return false

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
      values.put(MediaStore.Images.Media.IS_PENDING, Pending.DISABLED.value)
      contentResolver.update(contentUri, values, null, null)
    }

    return isSuccess
  }

  private fun getContentUri(
    resolver: ContentResolver,
    values: ContentValues,
    fileName: String
  ) = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
    getUriApiVersionQ(resolver, values)
  } else {
    getUri(resolver, fileName, values)
  }

  private fun getUri(
    resolver: ContentResolver,
    fileName: String,
    values: ContentValues
  ): Uri? {
    val file = File(
      Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
      DIRECTORY
    )
    if (!file.exists()) {
      file.mkdir()
    }
    values.put(
      MediaStore.Images.Media.DATA,
      File(file, fileName).absolutePath
    )
    return resolver.insert(
      MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
      values
    )
  }

  @RequiresApi(Build.VERSION_CODES.Q)
  private fun getUriApiVersionQ(
    resolver: ContentResolver,
    values: ContentValues
  ): Uri? {
    values.apply {
      put(
        MediaStore.Images.Media.RELATIVE_PATH,
        File(Environment.DIRECTORY_PICTURES, DIRECTORY).path
      )
      put(MediaStore.Images.Media.IS_PENDING, Pending.ENABLED.value)
    }
    return resolver.insert(
      MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY),
      values
    )
  }

  private enum class Pending(val value: Int) {
    ENABLED(1),
    DISABLED(0)
  }
}
