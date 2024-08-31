package com.example.album.misc

import kotlinx.datetime.Instant

object InstantParser {

  fun parse(dateTimeString: String): Instant {
    return Instant.parse(dateTimeString)
  }
}
