package com.example.album.infra.repository

import android.content.Context
import android.graphics.Bitmap
import com.example.album.model.Album
import com.example.album.model.Favorite
import com.example.album.model.Photo
import kotlinx.datetime.Instant

interface AlbumRepository {

  suspend fun loadAlbum(
    host: String,
    id: String,
    accessCode: String
  ) : Album

  suspend fun loadFavorite(
    host: String,
    id: String,
    accessCode: String
  ) : Favorite

  suspend fun addFavorite(
    host: String,
    id: String,
    accessCode: String,
    photo: Photo
  ): Boolean

  suspend fun savePhoto(
    context: Context,
    bitmap: Bitmap,
    shotDateTime: Instant,
    fileName: String
  ): Boolean
}
