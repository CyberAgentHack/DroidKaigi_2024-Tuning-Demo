package com.example.album.infra.datasource.api.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AlbumResponse(

  @SerialName("event_name") val eventName: String,
  @SerialName("photos") val photoList: List<PhotoResponse>
)

@Serializable
data class PhotoResponse(

  @SerialName("image_url") val imageUrl: String,
  @SerialName("shooted_date_time") val shotDateTime: String, // 2023-12-17T05:44:50Z
  @SerialName("file_name") val fileName: String
)
