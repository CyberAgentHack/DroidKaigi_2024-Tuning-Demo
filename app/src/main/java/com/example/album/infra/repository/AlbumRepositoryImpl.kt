package com.example.album.infra.repository

import android.content.Context
import android.graphics.Bitmap
import com.example.album.infra.datasource.api.PhotospeedwayApi
import com.example.album.infra.datasource.api.response.AlbumResponse
import com.example.album.infra.datasource.file.FileSaver
import com.example.album.misc.InstantParser
import com.example.album.model.Album
import com.example.album.model.Photo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.Instant
import javax.inject.Inject

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

  override fun savePhoto(
    context: Context,
    bitmap: Bitmap,
    shotDateTime: Instant,
    fileName: String
  ): Boolean {
    return fileSaver.saveFile(
      context = context,
      bitmap = bitmap,
      dateTime = shotDateTime,
      fileName = fileName
    )
  }
}
