package com.example.album.model

data class ExifInfo(
  val camera: String,
  val lens: String,
  val shutterSpeed: String,
  val focalLength: String,
  val aperture: String,
  val iso: String
) {
  val labeledShutterSpeed = "${shutterSpeed}sec"
  val labeledFocalLength = "${focalLength}mm"
  val labeledAperture = "F${aperture}"
  val labeledIso = "ISO $iso"
}
