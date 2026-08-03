package com.example.fintracker.data.remote.mapper

import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeParseException

internal fun <T : Any> T?.requireRemoteField(fieldName: String): T =
    this ?: throw RemoteDataException("Required field '$fieldName' is missing")

internal fun String.toRemoteBigDecimal(fieldName: String): BigDecimal =
    try {
        toBigDecimal()
    } catch (exception: NumberFormatException) {
        throw RemoteDataException("Field '$fieldName' is not a valid decimal", exception)
    }

// Поддерживает несколько ISO-форматов времени, встречающихся в ответах API.
internal fun String.toRemoteInstant(fieldName: String): Instant {
    val parsed = runCatching { Instant.parse(this) }
        .recoverCatching { OffsetDateTime.parse(this).toInstant() }
        .recoverCatching { LocalDateTime.parse(this).toInstant(ZoneOffset.UTC) }

    return parsed.getOrElse { exception ->
        val cause = exception as? DateTimeParseException ?: DateTimeParseException(
            "Invalid date-time",
            this,
            0,
            exception
        )
        throw RemoteDataException("Field '$fieldName' is not a valid ISO date-time", cause)
    }
}
