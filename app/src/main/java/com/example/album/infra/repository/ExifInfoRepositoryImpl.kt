package com.example.album.infra.repository

import com.example.album.infra.datasource.api.PhotospeedwayApi
import com.example.album.infra.datasource.api.response.ExifInfoResponse
import com.example.album.model.ExifInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.math.ceil

class ExifInfoRepositoryImpl @Inject constructor(
  private val photospeedwayApi: PhotospeedwayApi
) : ExifInfoRepository {

  override suspend fun getExifInfo(host: String, imageUrl: String): ExifInfo {
    val requestUrl = "$host/photoexif?image_url=$imageUrl"
    return withContext(Dispatchers.IO) {
      photospeedwayApi.getExifInfo(requestUrl).convertToExifInfo()
    }
  }

  private fun ExifInfoResponse.convertToExifInfo(): ExifInfo {
    return ExifInfo(
      camera = this.camera,
      lens = this.lens,
      shutterSpeed = this.shutterSpeed.tryConvertToFraction(),
      focalLength = this.focalLength,
      aperture = this.aperture,
      iso = iso
    )
  }

  private fun String.tryConvertToFraction(): String {
    val floatValue = this.toFloatOrNull() ?: return this
    if (floatValue >= 0.5) {
      return this
    }

    val denominator = ceil(1.0f / floatValue).toInt()
    return "1/${denominator}"
  }
}
