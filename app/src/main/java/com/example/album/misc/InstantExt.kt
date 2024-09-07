package com.example.album.misc

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

fun Instant.toLocalDateTimeInZone(timeZone: TimeZone = TimeZone.currentSystemDefault()): LocalDateTime {
  return this.toLocalDateTime(timeZone)
}

fun Instant.formatDateTime(): String {
  val localDateTime = this.toLocalDateTimeInZone()
  return "${localDateTime.year}年${localDateTime.monthNumber}月${localDateTime.dayOfMonth}日 ${localDateTime.formatHourMinuteSecond()}"
}

fun Instant.formatISO8601(): String {
  return this.toString()
}
