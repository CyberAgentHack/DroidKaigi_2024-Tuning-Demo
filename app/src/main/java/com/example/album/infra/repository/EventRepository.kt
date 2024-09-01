package com.example.album.infra.repository

import com.example.album.model.Event

interface EventRepository {

  suspend fun loadEvent(): List<Event>

  suspend fun saveEvent(event: Event)
}
