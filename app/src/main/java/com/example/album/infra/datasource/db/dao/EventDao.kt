package com.example.album.infra.datasource.db.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.album.infra.datasource.db.entity.EventEntity

@Dao
interface EventDao {

  @Query(
    """SELECT * FROM event"""
  )
  suspend fun getEvents(): List<EventEntity>

  @Upsert
  suspend fun upsertEvent(eventEntity: EventEntity)
}
