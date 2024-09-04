package com.example.album.infra.repository

import com.example.album.model.ExifInfo

interface ExifInfoRepository {

  suspend fun getExifInfo(
    host: String,
    imageUrl: String
  ): ExifInfo
}
