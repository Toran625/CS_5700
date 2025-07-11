package org.example.project

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

private val dateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy 'at' h:mm a")
    .withLocale(Locale.getDefault())
    .withZone(ZoneId.systemDefault())

fun formatTimestamp(timestamp: Long): String {
    return dateTimeFormatter.format(Instant.ofEpochMilli(timestamp))
}
