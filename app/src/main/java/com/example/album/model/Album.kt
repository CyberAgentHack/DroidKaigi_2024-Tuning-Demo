package com.example.album.model

import kotlinx.datetime.Instant

data class Album(
  val name: String,
  val photoList: List<Photo>
)

data class Favorite(
  val photoList: List<Photo>
)

data class Photo(
  val imageUrl: String,
  val shotDateTime: Instant,
  val fileName: String
)
