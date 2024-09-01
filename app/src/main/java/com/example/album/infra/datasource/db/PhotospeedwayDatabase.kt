package com.example.album.infra.datasource.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.album.infra.datasource.db.dao.EventDao
import com.example.album.infra.datasource.db.entity.EventEntity

@Database(
  entities = [EventEntity::class],
  version = PhotospeedwayDatabase.DATABASE_VERSION,
  exportSchema = true
)
abstract class PhotospeedwayDatabase : RoomDatabase() {
  abstract fun eventDao(): EventDao

  companion object {
    const val DATABASE_VERSION = 1
  }
}
