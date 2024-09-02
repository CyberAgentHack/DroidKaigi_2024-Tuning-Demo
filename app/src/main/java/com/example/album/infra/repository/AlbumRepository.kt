package com.example.album.infra.repository

import android.content.Context
import android.graphics.Bitmap
import com.example.album.model.Album
import kotlinx.datetime.Instant

interface AlbumRepository {

  suspend fun loadAlbum(
    host: String,
    id: String,
    accessCode: String
  ) : Album

  fun savePhoto(
    context: Context,
    bitmap: Bitmap,
    shotDateTime: Instant,
    fileName: String
  ): Boolean
}
