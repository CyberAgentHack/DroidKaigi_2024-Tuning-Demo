package com.example.album.infra.datasource.api.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ExifInfoResponse(
  @SerialName("camera") val camera: String,
  @SerialName("lens") val lens: String,
  @SerialName("shutter_speed") val shutterSpeed: String,
  @SerialName("focal_length") val focalLength: String,
  @SerialName("aperture") val aperture: String,
  @SerialName("iso") val iso: String
)
