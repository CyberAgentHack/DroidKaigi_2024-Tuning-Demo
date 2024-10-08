package com.example.album.infra.repository

import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.os.Build
import android.provider.MediaStore
import com.example.album.infra.datasource.api.PhotospeedwayApi
import com.example.album.infra.datasource.api.request.FavoriteRequest
import com.example.album.infra.datasource.api.response.AlbumResponse
import com.example.album.infra.datasource.api.response.PhotoResponse
import com.example.album.infra.datasource.file.FileSaver
import com.example.album.misc.InstantParser
import com.example.album.misc.formatISO8601
import com.example.album.model.Album
import com.example.album.model.Favorite
import com.example.album.model.Photo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.Instant
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.URL
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AlbumRepositoryImpl @Inject constructor(
  private val albumApi: PhotospeedwayApi,
  private val fileSaver: FileSaver
) : AlbumRepository {

  override suspend fun loadAlbum(host: String, id: String, accessCode: String): Album {
    val requestUrl = "$host/album?event=$id&access_code=$accessCode"
    return withContext(Dispatchers.IO) {
      albumApi.getAlbum(requestUrl).convertToAlbum()
    }
  }

  private fun AlbumResponse.convertToAlbum(): Album {
    return Album(
      name = this.eventName,
      photoList = this.photoList.map {
        Photo(
          imageUrl = it.imageUrl,
          shotDateTime = InstantParser.parse(it.shotDateTime),
          fileName = it.fileName
        )
      }
    )
  }

  override suspend fun loadFavorite(host: String, id: String, accessCode: String): Favorite {
    val requestUrl = "$host/favorite?event=$id&access_code=$accessCode"
    return withContext(Dispatchers.IO) {
      albumApi.getFavorite(requestUrl).convertToFavorite()
    }
  }

  private fun List<PhotoResponse>.convertToFavorite(): Favorite {
    return Favorite(
      photoList = this.map {
        Photo(
          imageUrl = it.imageUrl,
          shotDateTime = InstantParser.parse(it.shotDateTime),
          fileName = it.fileName
        )
      }
    )
  }

  override suspend fun addFavorite(host: String, id: String, accessCode: String, photo: Photo): Boolean {
    val requestUrl = "$host/favorite?event=$id&access_code=$accessCode"
    return withContext(Dispatchers.IO) {
      val result = albumApi.addFavorite(requestUrl, photo.convertToFavoriteRequest())
      return@withContext result.imageUrl == photo.imageUrl
    }
  }

  private fun Photo.convertToFavoriteRequest(): FavoriteRequest {
    return FavoriteRequest(
      imageUrl = this.imageUrl,
      shotDateTime = this.shotDateTime.formatISO8601(),
      fileName = this.fileName
    )
  }

  override suspend fun savePhoto(
    context: Context,
    bitmap: Bitmap,
    shotDateTime: Instant,
    fileName: String
  ): Boolean {
    return withContext(Dispatchers.IO) {
      fileSaver.saveFile(
        context = context,
        bitmap = bitmap,
        dateTime = shotDateTime,
        fileName = fileName
      )
    }
  }
}
