package com.example.album.infra.datasource.db.entity

import androidx.room.Entity

@Entity(
  tableName = "event",
  primaryKeys = ["host", "eventId"]
)
data class EventEntity(
  val host: String,
  val eventId: String,
  val name: String,
  val accessCode: String
)
