package com.example.album.misc

import kotlinx.datetime.LocalDateTime

fun LocalDateTime.formatHourMinuteSecond(): String {
  return "${this.hour.toString().padStart(2, '0')}:${this.minute.toString().padStart(2, '0')}:${this.second.toString().padStart(2, '0')}"
}
