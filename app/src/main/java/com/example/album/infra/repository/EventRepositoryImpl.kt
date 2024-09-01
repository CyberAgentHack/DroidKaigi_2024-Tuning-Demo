package com.example.album.infra.repository

import com.example.album.infra.datasource.db.PhotospeedwayDatabase
import com.example.album.infra.datasource.db.entity.EventEntity
import com.example.album.model.Event
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EventRepositoryImpl @Inject constructor(
  private val eventDatabase: PhotospeedwayDatabase
) : EventRepository {
  override suspend fun loadEvent(): List<Event> {
    return withContext(Dispatchers.IO) {
      eventDatabase.eventDao().getEvents().map { it.convertToModel() }
    }
  }

  override suspend fun saveEvent(event: Event) {
    return withContext(Dispatchers.IO) {
      eventDatabase.eventDao().upsertEvent(event.convertToEntity())
    }
  }

  private fun EventEntity.convertToModel(): Event {
    return Event(
      host = this.host,
      id = this.eventId,
      name = this.name,
      accessCode = this.accessCode
    )
  }

  private fun Event.convertToEntity(): EventEntity {
    return EventEntity(
      host = this.host,
      eventId = this.id,
      name = this.name,
      accessCode = this.accessCode
    )
  }
}
